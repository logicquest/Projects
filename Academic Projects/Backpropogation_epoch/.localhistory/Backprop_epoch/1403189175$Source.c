/***********************************************************/
/* This code implements backprop learning for a simple     */
/* two-layer perceptron with sigmoid() activation function */
/* It is neither particularly efficient nor particularly   */
/* elegant.                                                */
/*                                                         */
/* You may use it as a working, though limited, example    */
/* of how someone might implement backprop in a procedural */
/* language (in this case, ANSI C).                        */
/*                                                         */
/* Note that I've "hard coded" some items that an ideal    */
/* implemention would not have hard coded.  If you're      */
/* going to base any of your homework off of this, be      */
/* and modify my methods as think appropriate. ;)          */
/*                                                         */
/* -jcg                                                    */
/*                                                         */
/***********************************************************/


#include <stdlib.h>
#include <stdio.h>
#include <math.h>

/* Yes... it is poor practice to #include a source file like I do in the
next line (#include "./rng/rng.c").  Fix it if it bothers you :)
*/

#include <rng.h>

/* We're going to base our implementation around a combination
of structures that store information about individual neurons
and matricies that store the weights between layers of
neurons.  This is similar to the vector/matrix notiation
we've adopted in class and is fairly close to how we'd do
it in Matlab and/or Octave -- except of course here we're
responsible for maintaing data structures and walking them
with code loops.  Also, I'm NOT folding the biases into the
weight matrices.  Rather, I'm keeping them as separate values
held in fields inside each neuron. */


/* The following is the neuron structure.  We're storing output, gradient
(which corresponds to "little delta" in the book) and neuron
bias. A more mature implementation might also store the
type of transfer function the neuron uses and allow different neurons
to have different transfer functions.  In this code, I'm hard coding
the sigmoid() transfer functions.  A mature implementation might
allow a user to specify her/her preferred transfer function */


typedef struct neuron_s
{
	double output;        /* sigmoid(receptive_field_of_neuron) */
	double gradient;      /* little delta (local gradient) for this
						  neuron, computed presuming sigmoid()
						  transfer function */
	double bias;          /* In this implementation, we're NOT folding
						  the bias into the weight vector/matrix.
						  We're leaving it separate for clarity of
						  presentation */
} neuron_t;
typedef neuron_t *neuron_ptr;


/* Now we give the definition of the structure holding a whole neural network */

typedef struct network_s
{ /* Following are the counts of the number of elements in each
  of THREE layers.  One layer of input units, one layer of
  hidden neurons, and one layer of output neurons */

	int input_node_count;      /* The number of input units */
	int hidden_neuron_count;   /* The number of hidden layer neurons */
	int output_neuron_count;   /* The number of output layer neurons */

	/* Following are the actual elements in each layer.  We will
	represent the elements as arrays of the appropraite types.
	The input layer, for example, will be a simple array of
	floating point numbers, because input units are just
	"placeholders" for elements of the input vector.  The hidden
	layer and the output layer ARE made up of neurons, however.
	In this scheme, we're starting our indexing at 0 as per the
	normal C language convention.  Remember, in this code,
	biases are NOT folded into the weight matricies, so
	0 indexed elements are actual inter-unit weights. If we were
	using the "folded bias" convention, element zero would BE
	the neuron's bias.  Here, it is NOT.  Be warned ;) */

	double    *input_layer;    /* pointer to a block of input layer elements */
	neuron_t  *hidden_layer;   /* pointer to a block of neurons */
	neuron_t  *output_layer;   /* pointer to a block of neurons */
	
	/* Following are arrays to store weights.  Technically, these elements
	form matrices that have rows and columns equal in number to the
	number of elements in the layer the weights are coming from to
	and the number of elements in the layer the weights are going to.
	I'm storing them in flattened form in single dimension arrays,
	however.  No big... we'll just compute positions in the one dimensional
	weight arrays from 2 dimensional weight indices. */

	double *first_layer_weights;  /* Weights FROM input layer TO hidden layer  */
	double *second_layer_weights; /* Weights FROM hidden layer TO output layer */

	double *first_layer_delta_weigts;
	double *second_layer_delta_weigts;

	double *first_layer_delta_bias;
	double *second_layer_delta_bias;

} network_t;
typedef network_t  *network_ptr;


/***********************************************************/
/* math support routines                                   */
/***********************************************************/

double sigmoid(double x)

/* This function computes a simple sigmoid with min and max
outputs of 0.0 and 1.0 respectively. Its operation
should be fairly obvious.  Note that this implementation does
not support the alpha shape parameter for sigmoid.  Add it
if you think you need it. Refer to class lectures for the
location and use of the alpha parameter. */

{
	double exp();
	return 1.0 / (1.0 + exp(-x));
}

/***********************************************************/
/* memory allocation / and network support routines        */
/***********************************************************/

void malloc_network(int         input_nodes,
	int         hidden_nodes,
	int         output_nodes,
	network_ptr *network)

	/* This function does all the dynamic allocation needed for
	a two-layer perceptron as defined by struct network_s.
	Note that this routine just allocates memory.  It does
	not initialize the network with any preliminary values.
	This is done by init_network().  It does, however,
	set up node and weight memory allowing for a network
	of <input_nodes> input nodes, <hidden_nodes> hidden
	layer nodes, and <output_nodes> output neurons.

	NOTE: Though this should be obvious, this routine takes
	in a pointer to the pointer to the network structure.
	This is done so that this routine can change the user
	designated pointer to point to the newly allocated
	network memory.  If this is mystifying, please consult
	a basic C language reference.  Alternatively switch
	to a language that does not require pointer voodoo
	to get things done.
	*/

{
	*network = (network_t*)malloc(sizeof(network_t));
	(*network)->input_node_count = input_nodes;
	(*network)->hidden_neuron_count = hidden_nodes;
	(*network)->output_neuron_count = output_nodes;
	(*network)->input_layer = (double*)malloc(sizeof(double)*input_nodes);
	(*network)->hidden_layer = (neuron_t*)malloc(sizeof(neuron_t)*hidden_nodes);
	(*network)->output_layer = (neuron_t*)malloc(sizeof(neuron_t)*output_nodes);
	(*network)->first_layer_weights = (double*)malloc(sizeof(double)*input_nodes*hidden_nodes);
	(*network)->second_layer_weights = (double*)malloc(sizeof(double)*hidden_nodes*output_nodes);
	(*network)->first_layer_delta_weigts = (double*)malloc(sizeof(double)*input_nodes*hidden_nodes);
	(*network)->second_layer_delta_weigts = (double*)malloc(sizeof(double)*hidden_nodes*output_nodes);
	(*network)->first_layer_delta_bias = (double*)malloc(sizeof(neuron_t)*hidden_nodes);
	(*network)->second_layer_delta_bias = (double*)malloc(sizeof(double)*output_nodes);

}


void free_network(network_ptr *network)

/* This one should be pretty obvious as well.  It just unallocates
any dynamically allocated memory associated with the
given pointer.  Standard disclaimers apply.  Don't unallocate
something you didn't allocate... etc.

NOTE: This routine, like malloc_network(), also takes in
a pointer to a pointer.  This is because this routine,
after freeing any memory associated with the user designated
pointer, sets it back to NULL.  Again, consut a C reference if
this is mysterious.
*/

{
	free((*network)->second_layer_weights);
	free((*network)->first_layer_weights);
	free((*network)->output_layer);
	free((*network)->hidden_layer);
	free((*network)->input_layer);
	free((*network)->second_layer_delta_weigts);
	free((*network)->first_layer_delta_weigts);

	free(*network);
	*network = NULL;
}


void init_network_weights(network_ptr network, double max_weight_max, RNG *generator)

/* This routine takes in a pointer to an allocated network, a maximum weight value,
and a pointer to a random number generator struct (see rnd.c).  It walks the network
and pre-initializes all weights and biases to values in the range of -max_weight_max
to max_weight_max.  Be sure you pass in only network structs that have been
allocated via malloc_network().

If this were a mature implementation, this routine would actually initialize
the weights according to a heursitic that took into account the number of inputs
going into each neuron AND presuming that input patterns were preprocessed and
pre-scaled into a known range of numerical values.  See the book and class
notes for details.

*/

{
	int count;
	for (count = 0; count < (network->input_node_count * network->hidden_neuron_count); count++)
	{

		network->first_layer_weights[count] = rng_uniform(generator, -max_weight_max, max_weight_max);
		network->first_layer_delta_weigts[count] = 0.0;
	}

	for (count = 0; count < (network->hidden_neuron_count * network->output_neuron_count); count++)
	{

		network->second_layer_weights[count] = rng_uniform(generator, -max_weight_max, max_weight_max);
		network->second_layer_delta_weigts[count] = 0.0;
	}
	for (count = 0; count < network->hidden_neuron_count; count++)
	{

		network->hidden_layer[count].bias = rng_uniform(generator, -max_weight_max, max_weight_max);
		network->first_layer_delta_bias[count] = 0.0;
	}
	for (count = 0; count < network->output_neuron_count; count++){
		network->output_layer[count].bias = rng_uniform(generator, -max_weight_max, max_weight_max);
		network->second_layer_delta_bias[count]= 0.0;
	}


}


void print_network_parameters(network_ptr network)

/* This is just a utility / debugging routine that prints out all parameters
of a the network pointed at by <network>.  Since this is a simple multi-layer
perceptron, it is completely defined by the settings of all the biases and
inter-layer weights.  This routine just... ummm... prints those out
*/

{
	int j, k;
	printf("Input Nodes:  %d\n", network->input_node_count);
	printf("Hidden Nodes: %d\n", network->hidden_neuron_count);
	printf("Output Nodes: %d\n", network->output_neuron_count);
	printf("\n\n");
	printf("Hidden Layer Neurons\n");
	for (j = 0; j<network->hidden_neuron_count; j++)
		printf("     Hidden Neuron %d Bias: %f\n", j, network->hidden_layer[j].bias);
	printf("Hidden Layer Weights\n");
	for (j = 0; j<network->hidden_neuron_count; j++)
	for (k = 0; k<network->input_node_count; k++)
		printf("     hidden weight(%d, %d) = %f\n", j, k, network->first_layer_weights[j*network->input_node_count + k]);
	printf("Output Layer Neurons\n");
	for (j = 0; j<network->output_neuron_count; j++)
		printf("     Output Neuron %d Bias: %f\n", j, network->output_layer[j].bias);
	printf("Output Layer Weights\n");
	for (j = 0; j<network->output_neuron_count; j++)
	for (k = 0; k<network->hidden_neuron_count; k++)
		printf("     output weight(%d, %d) = %f\n", j, k, network->second_layer_weights[j*network->hidden_neuron_count + k]);

}

void print_network_outputs(network_ptr network)

/* Another utility routine.  This one just prints out the outputs and gradients
associated with every neuron in the network.
*/

{
	int j, k;
	printf("Input Nodes:  %d\n", network->input_node_count);
	printf("Hidden Nodes: %d\n", network->hidden_neuron_count);
	printf("Output Nodes: %d\n", network->output_neuron_count);
	printf("\n\n");

	printf("Hidden Layer Neurons\n");
	for (j = 0; j<network->hidden_neuron_count; j++)
		printf("     Hidden Neuron %d Output: %f    gradient %f\n", j, network->hidden_layer[j].output, network->hidden_layer[j].gradient);

	printf("Output Layer Neurons\n");
	for (j = 0; j<network->output_neuron_count; j++)
		printf("     Output Neuron %d Output: %f    gradient %f\n", j, network->output_layer[j].output, network->output_layer[j].gradient);

}

/*************************************************************************/
/* Perceptron Computation Routines (I.E. The important stuff)            */
/*************************************************************************/

void compute_network_outputs(double *x, network_ptr network)

/* This routine takes in a vector of inputs (*x), copies them into the input
units of the supplied network, and then propagates the inputs forward through
the network (the feedforward phase).  The output of each neuron is
placed in the 'output' field of each neuron.  Subsequent calls to this
routine will, of course, overwrite those values with new outputs
determined by the inputs provided on the new call
*/

{
	int j, k;
	double receptive_field;

	/* Apply inputs to network by copying them into the network's input
	units.  Note, we're assuming here that the vector of inputs
	provided as a parameter has the same length as the number
	of input units in the network.*/

	for (j = 0; j < network->input_node_count; j++)
		network->input_layer[j] = x[j];


	/* Ok... now compute the outputs of all the neurons in the hidden layer.  Note
	that weight references are being flattened into a single dimension array using
	row-major ordering. */

	for (j = 0; j < network->hidden_neuron_count; j++)
	{
		receptive_field = 0.0;
		for (k = 0; k < network->input_node_count; k++)
			receptive_field += network->input_layer[k] * network->first_layer_weights[j*network->input_node_count + k];
		receptive_field += network->hidden_layer[j].bias;
		network->hidden_layer[j].output = sigmoid(receptive_field);
	}

	/* ...and.... compute the outputs of all the neurons in the output layer */

	for (j = 0; j < network->output_neuron_count; j++)
	{
		receptive_field = 0.0;
		for (k = 0; k < network->hidden_neuron_count; k++)
			receptive_field += network->hidden_layer[k].output * network->second_layer_weights[j*network->hidden_neuron_count + k];
		receptive_field += network->output_layer[j].bias;
		network->output_layer[j].output = sigmoid(receptive_field);
	}
}

void compute_network_gradients(double *d, network_ptr network)

/* This routine, given a vector of _desired_ outputs, will compute gradients for
all nodes in the network pointed at by the network_ptr <network>.  This
routine corresponds to the "backward pass" through the network.
Note that this routine makes use of the input vector and neural outputs
_already_ stored in the network.  It presumes you made a call to
compute_network_outputs() to fill those values in.
*/

{
	int j, k;
	double gradient_accumulator;

	//printf("------> %lf %lf\n", d[0], d[1]);
	// printf("-=-=-=-> %lf %lf\n",network->output_layer[0].output, network->output_layer[1].output);

	/* First, let's do the easy part.  Compute the gradients for all output nodes */
	for (j = 0; j<network->output_neuron_count; j++)
		network->output_layer[j].gradient = (d[j] - network->output_layer[j].output);

	/* Now, let's backpropagate the gradients into the hidden layer */
	for (k = 0; k<network->hidden_neuron_count; k++)
	{
		gradient_accumulator = 0.0;
		for (j = 0; j<network->output_neuron_count; j++)
			gradient_accumulator += (network->output_layer[j].gradient *network->second_layer_weights[j*network->hidden_neuron_count + k]);
		network->hidden_layer[k].gradient = network->hidden_layer[k].output *
			(1.0 - network->hidden_layer[k].output) *
			gradient_accumulator;
	}

}


void apply_delta_rule(network_ptr network, double learning_rate)

/* This routine will compute and apply delta_w's to all the weights and biases in the
network.  It PRESUMES that you have already ran compute_network_outputs()
and compute_network_gradients() to handle both the forward propagation of inputs
and the back propagation of gradients.  This routine will use inputs and gradients
alreadly stored in the network by those other two calls to do its work.
Note, this code (obviously) presumes a set learning rate that is universal
to all neurons.  You'll have to hack this a bit if you want/need something
else
*/

{
	int j, k;

	for (j = 0; j<network->output_neuron_count; j++)
	{
		for (k = 0; k < network->hidden_neuron_count; k++){


			network->second_layer_weights[j*network->hidden_neuron_count + k] += (network->second_layer_delta_weigts[j*network->hidden_neuron_count + k] / 64);
			network->second_layer_delta_weigts[j*network->hidden_neuron_count + k] = 0.0;
		}
		network->output_layer[j].bias += (network->second_layer_delta_bias[j] / 64);

		network->second_layer_delta_bias[j] = 0.0;
			
		
	}



	for (j = 0; j<network->hidden_neuron_count; j++)
	{
		for (k = 0; k < network->input_node_count; k++){

			
			network->first_layer_weights[j*network->input_node_count + k] += (network->first_layer_delta_weigts[j*network->input_node_count + k] / 64);
			network->first_layer_delta_weigts[j*network->hidden_neuron_count + k] = 0.0;

		}


		network->hidden_layer[j].bias += (network->first_layer_delta_bias[j] / 64);
		network->first_layer_delta_bias[j] = 0.0;
	}


	

}


int main()
{
	int c;
	int random_index;
	int random_class;
	double Y1, Y2;

	network_ptr my_network;  /* This is a pointer to the neural net structure that will
							 store our perceptron */

	RNG *rng_one;            /* This holds the current state of a random number generator.
							 see rnd.c if you interested */


	/* Define Input Units */
	double X[1][2];

	/* Define Corresponding Desired Outputs (XOR outputs) */
	double D[3][2] = { { 0.05, 0.05 },
	{ 0.05, 0.95 },
	{ 0.95, 0.05 } };

	/* Define Class One Input Vectors */
	double class_01[16][2] = { { -5.18, -5.35 },
	{ -4.53, -2.32 },
	{ -1.90, -1.80 },
	{ -3.80, -2.29 },
	{ -0.81, 0.46 },
	{ 1.14, -1.69 },
	{ -0.79, 2.80 },
	{ 2.15, 0.97 },
	{ 4.24, 4.43 },
	{ 2.16, 3.76 },
	{ 5.93, 3.17 },
	{ 4.30, 4.97 },
	{ 6.73, 6.71 },
	{ 6.47, 7.45 },
	{ 10.38, 8.71 },
	{ 10.77, 8.42 } };

	/* Define Class Two Input Vectors */
	double class_02[32][2] = { { -25.86, 16.78 },
	{ -22.13, 16.41 },
	{ -22.49, 17.52 },
	{ -20.47, 16.70 },
	{ -21.17, 19.82 },
	{ -20.62, 20.93 },
	{ -18.44, 19.99 },
	{ -16.49, 21.96 },
	{ -15.60, 24.03 },
	{ -15.11, 25.53 },
	{ -16.99, 25.88 },
	{ -15.00, 26.42 },
	{ -12.02, 28.35 },
	{ -13.72, 28.11 },
	{ -12.53, 27.80 },
	{ -9.24, 31.88 },
	{ 14.29, -25.15 },
	{ 14.86, -23.98 },
	{ 17.81, -21.47 },
	{ 19.69, -21.54 },
	{ 20.57, -20.38 },
	{ 21.48, -20.41 },
	{ 19.01, -20.28 },
	{ 23.39, -17.17 },
	{ 23.07, -15.62 },
	{ 25.95, -14.36 },
	{ 24.72, -15.22 },
	{ 25.32, -15.81 },
	{ 25.53, -14.74 },
	{ 26.45, -12.21 },
	{ 30.94, -9.23 },
	{ 30.65, -8.30 } };

	/* Define Class Three Input Vectors */
	double class_03[16][2] = { { -40.82, -38.45 },
	{ -40.97, -40.46 },
	{ -38.55, -37.16 },
	{ -38.34, -38.00 },
	{ -36.02, -36.73 },
	{ -33.14, -35.97 },
	{ -32.51, -34.50 },
	{ -32.84, -34.33 },
	{ -34.92, -35.84 },
	{ -34.46, -37.86 },
	{ -36.42, -38.53 },
	{ -39.26, -36.34 },
	{ -38.35, -40.79 },
	{ -41.45, -40.80 },
	{ -41.52, -40.21 },
	{ -41.39, -41.88 } };

	double error_allowed;

	void compute_delta_weights(network_ptr my_network, double learning_rate);

	/* Set up random number generator */
	rng_one = rng_create();

	/* Set up neural network */
	malloc_network(2, 8, 2, &my_network); /* Two input nodes, two hidden nodes, one output node */

	/* Randomize initial weights */
	init_network_weights(my_network, 0.1, rng_one);

	/* Ok... let's let this guy learn the classification.  We proceed by selecting
	which of the three classes we want to draw the training sample, then selecting
	from that class a random sample.  Then we do one pass of backprop learning
	with that sample.  Oh... in this code we do that five million times.  In
	a mature implementation, we wouldn't hard code the number of steps.  Rather,
	we'd make a conditional loop that stopped on some condition like, achieving
	some low error or not lowering error in some time.  Refer to the lectures and
	to the reading.
	*/
	

	//do
	//{
		
		//Training one Epoch

do
	{

		for (c = 0; c < 16; c++)
		{

			compute_network_outputs(class_01[c], my_network);                     /* feed inputs forward  */
			compute_network_gradients(D[0], my_network);							/* compute gradients       */
			compute_delta_weights(my_network, 0.2);
		}

		

	//	print_network_parameters(my_network);

		for (c = 0; c < 32; c++)
		{
			compute_network_outputs(class_02[c], my_network);                     /* feed inputs forward  */
			compute_network_gradients(D[1], my_network);							/* compute gradients       */
			compute_delta_weights(my_network, 0.2);
		}


		for (c = 0; c < 16; c++)
		{
			compute_network_outputs(class_03[c], my_network);                     /* feed inputs forward  */
			compute_network_gradients(D[2], my_network);							/* compute gradients       */
			compute_delta_weights(my_network, 0.2);
		}


		
		apply_delta_rule(my_network, 0.2);

		//printf("%f\n",my_network->output_layer[0].gradient);
	error_allowed = 0.0005;

		} while ((my_network->output_layer[0].gradient>error_allowed || my_network->output_layer[0].gradient<-error_allowed) && ((my_network->output_layer[1].gradient>error_allowed || my_network->output_layer[1].gradient<-error_allowed)));

	


	

	/* Now, let's see how we did by checking all the inputs against what the trained network
	produces */

	printf("\nNetwork Outputs for Each Training Pattern\n");
	printf("-----------------------------------------\n");

	printf("Class One\n");
	printf("---------\n");
	for (c = 0; c<16; c++)
	{
		X[0][0] = class_01[c][0];
		X[0][1] = class_01[c][1];
		compute_network_outputs(X[0], my_network); /* Feed input pattern forward */
		Y1 = my_network->output_layer[0].output;   /* Get output of output layer node 0 */
		Y2 = my_network->output_layer[1].output;   /* Get output of output layer node 1 */
		printf("X1 = %10f   X2=%10f   Y1=%10f   Y2=%10f\n", X[0][0], X[0][1], Y1, Y2); /* Print outputs generated by trained network */
	}

	printf("\nClass Two\n");
	printf("---------\n");
	for (c = 0; c<32; c++)
	{
		X[0][0] = class_02[c][0];
		X[0][1] = class_02[c][1];
		compute_network_outputs(X[0], my_network); /* Feed input pattern forward */
		Y1 = my_network->output_layer[0].output;   /* Get output of output layer node 0 */
		Y2 = my_network->output_layer[1].output;   /* Get output of output layer note 1 */
		printf("X1 = %10f   X2=%10f   Y1=%10f   Y2=%10f\n", X[0][0], X[0][1], Y1, Y2); /* Print outputs generated by trained network */
	}

	printf("\nClass Three\n");
	printf("------------\n");
	for (c = 0; c<16; c++)
	{
		X[0][0] = class_03[c][0];
		X[0][1] = class_03[c][1];
		compute_network_outputs(X[0], my_network); /* Feed input pattern forward */
		Y1 = my_network->output_layer[0].output;   /* Get output of output layer node 0 */
		Y2 = my_network->output_layer[1].output;   /* Get output of output layer note 1 */
		printf("X1 = %10f   X2=%10f   Y1=%10f   Y2=%10f\n", X[0][0], X[0][1], Y1, Y2); /* Print outputs generated by trained network */
	}

	printf("\n");
	printf("Perceptron Architecture \n");
	printf("-----------------------------------------\n");

	//print_network_parameters(my_network);

	free_network(&my_network);

}

void compute_delta_weights(network_ptr my_network,double learning_rate)
{
	int j, k;
	int somevalue;

	for (j = 0; j<my_network->output_neuron_count; j++)
	{
		for (k = 0; k<my_network->hidden_neuron_count; k++)
			my_network->second_layer_delta_weigts[j*my_network->hidden_neuron_count + k] += (learning_rate *my_network->output_layer[j].gradient *my_network->hidden_layer[k].output);

		my_network->second_layer_delta_bias[j] += (learning_rate * my_network->output_layer[j].gradient);

	}


	


	
	for (j = 0; j<my_network->hidden_neuron_count; j++)
	{
		for (k = 0; k < my_network->input_node_count; k++){

						
			my_network->first_layer_delta_weigts[j*my_network->input_node_count + k] += (learning_rate *	my_network->hidden_layer[j].gradient *	my_network->input_layer[k]);
					
		}
		my_network->first_layer_delta_bias[j] += (learning_rate * my_network->hidden_layer[j].gradient);

	}


	




}