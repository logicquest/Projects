
/****************************************************************************/
/* Program:     Evolutionary Strategies Demo                                */
/* Programmer:  John C. Gallagher                                           */
/* Version:     3.0                                                         */
/* Date:        July 16, 2012                                               */
/* Purpose:     This program is a VERY simple demo of one form of an        */
/*              evolutionary strategies search.  Again, this is meant to    */
/*              be a functional demo, not an optimized piece of production  */
/*              code.  Optimizations and assorted awesomness are up to you  */
/*                                                                          */
/* Dependencies: This code depends on jcg's ran.c library.  In this sample  */
/*               implementation, I'm #including the code.  Yes.. that's     */
/*               evil and horrible.  In any production system you should    */
/*               compile separately and link.                               */
/*                                                                          */
/****************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include "rng.h"
#include "rng.c"

/*****************************************************************************/
/* Data Structures and Type Definitions                                      */
/*****************************************************************************/

/* In this case, a genome will be a vector of floating point values to be evolved,
followed by mutation rates, and then by alpha parameters.  A genome, therefore,
will have a length of 2 times the number of values you are attempting to
evolve if we're allowing a unique mutation rate for each x position.
Consult page 73 of the text for the general form of the genome.  Note we're
using one mutation rate per x parameter and we are NOT using the
alpha / rotation genome items */

typedef struct genome_s
{
	int genome_len;
	double *x;			/* These are the values being evolved */
	double *sigma;		/* Mutation rate for each x value */
	double fitness;		/* Genome fitness */

} genome_t;

/* Not surprisingly, a population will be represented as a collection of genomes
This is also a dynamically allocated structure.

Note that in evolutionary stratgies, we don't directly choose mutation rates
for parameters (the x's that are actually the targets of the search and
which are scored with an evaluation/objective function.  Rather, the mutation
rates that actually control the mutation of the x values live on the genome
with the x's and are themselves mutated and subjected to selection pressure.

That being said, we DO set some parameters that govern how agressively the
mutation rates are themselves mutated.  In a sense, we can set the "mutation
rates of the mutation rates that govern how agressively we mutate the x's".
This is a good thing, because it allows selection pressure to directly
govern how agressively moves in x parameter space should be.

That was kind of a mouthfull....  let's look at the numbers we CAN set
specifically:

member_count : The number of genomes in the population.

tau_prime and these govern the "agressiveness" of changes we make that the
indivdual mutation rates (sigmas) that are used to mutate the x positions.
Take a look at equation 4.4 on page 76
of the book.  Note that every change in a mutation rate (little sigma prime) is
computed by multiplying the current mutation rate (little sigma) times
a scaling factor.  That scaling factor is e to the "something" power.
That "something" is made of two parts.  One of them is a random number
generated for the WHOLE genome times tau prime and the other is a random number
generated for each sigma value uniquely times tau.  In a sense, "tau prime"
turns up the volume on how much you want the probability cloud around the
mutation rate to be "symetric" and "tau" turns up the volume on how much you
want that cloud to be "jaggy".  Contact me if you don't see this after some
reflection.

Ok... that being said.... as a user of an ES you'll rarely set these values directly.
Take a look at the paragraph immediately after equation 4.5 on page 76.
You'll see a heuristic means of setting tau and tau prime.  We use that heursic
in this code.

Ok.. now sigma epsilon.  When we're mutating the x values, we generally do not want
zero mutation rates... I mean... if you're rate is zero, what the point of mutating
at all?  Many ES implementations set a lower bound on the value of each sigma to
ensure that some "small moves" always happen.  THIS value is something a user
would think about setting based on the nature of the problem.  Consult page 76 for
more details.

*/

typedef struct population_s
{
	int		member_count;   /* The number of genomes in this population */
	double	tau_prime;      /* The tau associated with the whole of a genome */
	double	tau;            /* The tau associated with moves of individual sigmas */
	double	sigma_epsilon;  /* The minimum move we're willing to make on a mutation */
	genome_t *member;
} population_t;



/********************************************************************************/
/* Population Memory Allocation Routines                                        */
/* These are utility routines that are used to allocate and deallocate memory   */
/* for populations.  There are also individual genome allocation routines, but  */
/* those should rarely, if ever, be called by a casual user.  Note that a       */
/* population MUST be allocaed before it's used by any of the SGA processing    */
/* routines.                                                                    */
/********************************************************************************/

void ES_Genome_Malloc(genome_t *genome, int length)
/* This routine allocates memory for a genome of <length> bits.  It should be passed
a POINTER to the genome pointer so that the genome pointer can be modified to point
to the allocated character block.... */

{
	(*genome).x = (double *)malloc(sizeof(double)*length);
	(*genome).sigma = (double *)malloc(sizeof(double)*length);
	(*genome).genome_len = length;
	(*genome).fitness = 0.0;
}


void ES_Genome_Free(genome_t *genome)
/* This routine unallocates the memory used by a genome */
{
	free((*genome).x);
	free((*genome).sigma);
	(*genome).genome_len = 0;
}

void ES_Population_Malloc(population_t *population, int member_count, int genome_length)
/* This routine allocates a population of <member_count> bit string genomes of length
<genome_length>.  The population size and genome length will be stored in the population
structure, so you need not keep track of those numbers yourself */

{
	int member_c;

	(*population).member_count = member_count;
	(*population).tau_prime = 1.0 / sqrt(2.0 * (double)member_count);
	(*population).tau = 1.0 / sqrt(2.0 * sqrt((double)member_count));
	(*population).sigma_epsilon = 0.01;  // Heh.... I never set this in code I sent you BEFORE
	(*population).member = (genome_t *)malloc(member_count * sizeof(genome_t));
	for (member_c = 0; member_c < member_count; member_c++)
		ES_Genome_Malloc(&((*population).member[member_c]), genome_length);
}

void ES_Population_Free(population_t *population)
/* This routine frees memory allocated to a population. */

{
	int member_c;

	for (member_c = 0; member_c < (*population).member_count; member_c++)
		ES_Genome_Free(&((*population).member[member_c]));
	free(population->member);
	population->member = NULL;
}

/********************************************************************************/
/* Population Utility Routines                                                  */
/* These are all utility routines that manipulate populations and genomes.      */
/* Mostly these are things meant to copy, merge, or print information about     */
/* populations.  They are not "GA" functions specifically, but are used to      */
/* get status information and/or to implement portions of GA functions          */
/********************************************************************************/


void ES_Genome_Print(genome_t *genome)
/* This is a utility routine that just prints out the WHOLE genome (x's, sigmas,
objective function score.  This might suck to run on large genomes ;) ) */

{
	int value_count;

	for (value_count = 0; value_count < (*genome).genome_len; value_count++)
		printf("%lf ", (*genome).x[value_count]);

	printf(" | ");

	for (value_count = 0; value_count < (*genome).genome_len; value_count++)
		printf("%lf ", (*genome).sigma[value_count]);

	printf(" | ");

	printf(" (%lf)\n", (*genome).fitness);

}

void ES_Population_Print(population_t *population)
/* Prints out ALL the genomes in a population, */
{
	int p_count;
	int best_index;
	double best_score = -1e25;

	for (p_count = 0; p_count < (*population).member_count; p_count++)
	{
		ES_Genome_Print(&((*population).member[p_count]));
		if (((*population).member[p_count]).fitness > best_score)
		{
			best_score = ((*population).member[p_count]).fitness;
			best_index = p_count;
		}
	}

	printf("\n\n");
	ES_Genome_Print(&((*population).member[best_index]));
}

void ES_Genome_Copy(genome_t *source_genome, genome_t *destination_genome)
/* Copies the infornmation in <*source_genome> to <*destination_genome>.  Note
that it is ASSUMED that both genomes have been alloacated memory.  The
source genome is left unmolsested and any information in the destination
genome is overwritten with a copy of the information in the source
genome */

{
	int value_pos;
	if ((*source_genome).genome_len != (*destination_genome).genome_len)
	{
		fprintf(stderr, "ERROR in ES_Genome_Copy: Source and Destination of Unequal Length\n");
		exit(0);
	}

	(*destination_genome).fitness = (*source_genome).fitness;
	for (value_pos = 0; value_pos<(*destination_genome).genome_len; value_pos++)
	{
		(*destination_genome).x[value_pos] = (*source_genome).x[value_pos];
		(*destination_genome).sigma[value_pos] = (*source_genome).sigma[value_pos];
	}
}

void ES_Population_Copy(population_t *source_population, population_t *destination_population)
/* This copies one population to another.  It works like ES_Genome_Copy(), except
for whole populations */
{
	int p_count;
	if ((*source_population).member_count != (*destination_population).member_count)
	{
		fprintf(stderr, "ERROR in ES_Population_Copy: Source and Destination of Unequal Sizes\n");
		exit(0);
	}
	for (p_count = 0; p_count < (*source_population).member_count; p_count++)
		ES_Genome_Copy(&((*source_population).member[p_count]), &((*destination_population).member[p_count]));
}


void ES_Population_Select_Copy(population_t *source_population, population_t *destination_population, RNG *rng)
/* This routine is used to do selection of the parents into the child population.  In this case, we're generally
assuming that the parent population is smaller than the child population (each parent makes multiple
children).  This, however, need not be the case for this code to work.  What the code does is that for
every slot in the CHILD population, a member of the PARENT population is randomly selected with a uniform
probability and COPIED into one of the slots of the CHILD population.  No mutation or crossover is done here,
just copying.  In the terms of the book, the parent population is the mu population and the child popultion
is the lambda popultion */

{
	int p_count;
	int source_index;
	for (p_count = 0; p_count < (*destination_population).member_count; p_count++)
	{
		source_index = (int)floor(rng_uniform(rng, 0.0, (double)((*source_population).member_count)));
		//printf("source_index = %d  p_count = %d\n", source_index, p_count);
		ES_Genome_Copy(&((*source_population).member[source_index]), &((*destination_population).member[p_count]));
	}
}

double ES_Population_Best_Copy(population_t *source_population, population_t *destination_population)
/* This one is a little odd.  This code implements survivor selection using a (mu, lamda) strategy.  It will
copy the "n best" genomes FROM source population (the child LAMBDA population) into the n slots of the
destination population (the parent population, or rather, what will become the parent on the next iteration).
This code does an insertion sort of the candidates and is efficent only when the size of the mu (parent)
population is much less than the size of the child population (lambda population).  Since this is generally
true in ES, I guess it's not a bad assumption.  If you were ever to use mu and lambdas of more equal size,
you might want to run an actual nlogn sort on the lambda population, then copy them in order back into mu.

Note that the mechanics of this code are "destructive" in that the fitnesses of members of the source
population (lambda) are overwritten.  This isn't a big deal as by the time we're doing selection, we're
done with that info anyway.  Still, be warned */

{
	int d_count, s_count;
	int best_index;
	double best_score;
	double champion_score = 0.0;

	if ((*source_population).member_count <= (*destination_population).member_count)
	{
		fprintf(stderr, "ERROR in ES_Population_Best_Copy: Source is smaller than Dest\n");
		exit(0);
	}

	for (d_count = 0; d_count < (*destination_population).member_count; d_count++)
	{
		best_score = -1e25;
		for (s_count = 0; s_count < (*source_population).member_count; s_count++)
		{
			if (((*source_population).member[s_count]).fitness > best_score)
			{
				best_score = ((*source_population).member[s_count]).fitness;
				best_index = s_count;
				if (d_count == 0) champion_score = best_score;
			}
		}
		ES_Genome_Copy(&((*source_population).member[best_index]), &((*destination_population).member[d_count]));
		((*source_population).member[best_index]).fitness = -1e25;

	}
	return(champion_score);
}




/********************************************************************************/
/* Population Initialization Routines                                           */
/* These functions provide initialization functionality for the SGA.  They      */
/* either randomize all the genomes in a population or a randomize a single     */
/* genome.  Note that you'll almost never need to randomize a genome directly,  */
/* as that functionality is included in the initialize population function.     */
/********************************************************************************/

void ES_Genome_Init(genome_t *genome, double min, double max, RNG *rng)
/* This routine sets an already allocated genome to contain random values.
We're doing this in a pretty generic manner here.  The x's get set from
a uniform probabilty distribution with max and min passed in from the user.
The initial sigmas are all being set to 1.0.  Good choice or bad?  Hell if
I know.  In a real application one might set those using some heuristic
or just count on the ES to evolve those sigmas to good values under its
own operation.  Oh... we're also setting fitness scores to 0.0.  This
may or may not be "right" with respect to a specific problem.  In actuality,
"real" implementations often have a flag to mark if the genome had been
evaluated.  This means one would ignore whatever score number was there
without doing a genome eval.  Sometimes people elaborate that flag into
a timer that keeps track of "how long since last evaluated".  With that kind
of timer, a genome could be re-evaluated every now and again just in case
things changed since the last time it was so evaluated.  Neat, huh? */

{
	int value_count;
	for (value_count = 0; value_count < (*genome).genome_len; value_count++)
	{ /* x's are randomized uniformly */
		(*genome).x[value_count] = rng_uniform(rng, min, max);
		/* Iniital mutation rate is set to standard dev of 1.0 */
		(*genome).sigma[value_count] = 1.0;
	}
	(*genome).fitness = 0.0;
}

void ES_Population_Init(population_t *population, double min, double max, RNG *rng)

/* This function randomizes all the genomes in a popuation.  It also sets each
of those genomes fitness score to 0.0 */

{
	int m_count;
	for (m_count = 0; m_count < (*population).member_count; m_count++)
		ES_Genome_Init(&((*population).member[m_count]), min, max, rng);
}


/********************************************************************************/
/* Genome Variation Functions                                                   */
/* Mutation Functions.... No crossover JUST yet                                 */
/********************************************************************************/

/* In ES, "mutation" happens in two stages.  The x's (the values that are the
target of the objective function) are mutated using rates that live on the
genome with the x values themselves.  The sigmas (the mutation rates associated
with each X) are mutated with agressiveness dictated by the two taus (see
earlier comments) and sigma_eplison (also see earlier comments).  In this code,
we're breaking out the mutation of x's and the mutation of sigmas into
two different bits of code to help make the distinction clear.  Generally,
we mutate the sigmas first and then mutate the x's with the new sigmas.
If might be a fun exercise to think about what happens if we reverse that...
*/


void ES_Genome_X_Mutate(genome_t *genome, RNG *rng)
/* This function will mutate each X value in the genome with a probability
specified by mutation informaion in the genome. This corresponds to equation 4.5
in the book.  Basically each x position is being modified by drawing a random
number from a standard normal distribution and scaling that by its corresponding
sigma.  The sigma associated with each x controls the size of the cloud of
probability around each x value.  Since there are multiple sigmas, that cloud
can be ellipsoid.  Since we're not using alphas (rotation), the clouds MUST
have axes parallel to the principle axes of the space.  If that doesn't make
sense, talk to me -- but don't sweat it too much for the midterm. */

{
	int value_count;
	double delta_x;
	double random_normal_value;

	for (value_count = 0; value_count < (*genome).genome_len; value_count++)
	{
		random_normal_value = rng_gaussian(rng, 0.0, 1.0);
		delta_x = (*genome).sigma[value_count] * random_normal_value;
		(*genome).x[value_count] += delta_x;
	}

}

void ES_Genome_Sigma_Mutate(genome_t *genome, double tau_prime, double tau, double sigma_epsilon, RNG *rng)
/* Now, here's a little mojo.  here we're implementing equation 4.4 from the book.
This isn't really difficult as much as it is a little tedious.  Basically we
generate a random number for the whole genome, then enter a for loop
that applies the changes to each sigma according to equation 4.4.
*/

{
	int value_count;
	double delta_sigma;
	double random_normal_value_prime;
	double random_normal_value;

	/* First generate the random number that will be applied to changing ALL
	the sigmas in this genome */

	random_normal_value_prime = rng_gaussian(rng, 0.0, 1.0);

	/* Now, walk through the genome applying equation 4.4 to each sigma.  Note that
	we use the "global" random number with tau prime and a "locally" generated
	random number with tau.  Also note that any sigma that is less than
	sigma_epsilon is set to sigma_epsilon.  It is here that we enforce that
	"minimum mutation rate". */

	for (value_count = 0; value_count < (*genome).genome_len; value_count++)
	{
		random_normal_value = rng_gaussian(rng, 0.0, 1.0);

		(*genome).sigma[value_count] *= exp(tau_prime * random_normal_value_prime + tau * random_normal_value);

		if ((*genome).sigma[value_count] < sigma_epsilon)
			(*genome).sigma[value_count] = sigma_epsilon;

	}

}



void ES_Population_Mutate(population_t *population, RNG *rng)
/* This function goes through a whole population and mutates all values and mutation
rates on the genome.  It is "destructive" in that it overwrites the population you
send in mutated versions.  Note that technically, you WILL have to
run the evaluation function on the genome yourself if you want an accurate
fitness value to be stored in there... */


{
	int m_count;

	for (m_count = 0; m_count < (*population).member_count; m_count++)
	{
		ES_Genome_Sigma_Mutate(&((*population).member[m_count]), (*population).tau_prime, (*population).tau, (*population).sigma_epsilon, rng);
		ES_Genome_X_Mutate(&((*population).member[m_count]), rng);
	}

}





/********************************************************************************/
/* Genome Fitness Functions                                                     */
/* Here's a bunch of fitness functions for you to play with.  Note you should   */
/* only use ONE of these for any run :).  All of them take in a genome and      */
/* return a double precision fitness value.  Bigger is better.                  */
/********************************************************************************/

double ES_Max_Ones_Fitness(genome_t *genome)
/* This one's simple...  just count the number of 1's in the genome.  More 1's
are better and produce a bigger fitness score.  Since the genomes are now
real valued, we're actually just counting the number of x positions that
are "near" a value of 1.0.  Since they're floats, we're only requireing
that the value is withing 1% of 1.0

You might note that this can be a pretty hard function for an ES to optimze.
You might also note that this was a pretty easy one (at least as we implemented
it before) for SGA.  Speculating on WHY this is might be a good exercise
to prepare for an exam.
*/

{
	int value_position;
	double one_count = 0.00;

	for (value_position = 0; value_position < (*genome).genome_len; value_position++)
	if ((*genome).x[value_position] > 0.99 && (*genome).x[value_position] < 1.01)
		one_count += 1.0;

	return one_count;
}


double ES_Rosenbrock(genome_t *genome)
/* This one's a nightmare, albeit a classic nightmare.
Check out http://en.wikipedia.org/wiki/Rosenbrock_function
The Rosenbrock function has a minimum at f(x,y) = f(1,1).
Finding that minimum is a minimization problem that we
can turn into an maximization problem by multiplying the
function's value by -1.  That's what we do here so that
our SGA maximizer can deal with it.

This function takes the first half of the genome and treats
it as a fixed-point representation of a number between -100 and 100.
it takes the second half of the genome and treats it as another
number between -100 and 100.  The first half of the genome is
assigned to x and the second to y.  If the genome is of odd
length, then the last bit is allocated to the representation of
the y allele.

oh... the rosenbrock function is:

f(x,y) = (1-x)^2 + 100(y-x^2)^2

f(1,1) = 0 which is the minimum....  to find that minimum,
we could MAXIMIZE the function -f(x,y), which is:

-[(1-x)^2 + 100(y-x^2)^2]

*/

{
	double x, y;
	int genome_length;
	double rosenbrock;
	int halfway_position;

	x = (*genome).x[0];
	y = (*genome).x[1];
	rosenbrock = pow((1.0 - x), 2.0) + 100.0*pow((y - x*x), 2.0);
	return(-rosenbrock);
}


double ES_Rosenbrock_4D(genome_t *genome)
{
	double dW = (*genome).x[0];
	double dX = (*genome).x[1];
	double dY = (*genome).x[2];
	double dZ = (*genome).x[3];
	double dRosenbrock4D = 0.0;

	dRosenbrock4D = pow((1.0 - dW), 2.0) + 100.0 * pow((dX - dW*dW), 2.0) +
		pow((1.0 - dX), 2.0) + 100.0 * pow((dY - dX*dX), 2.0) +
		pow((1.0 - dY), 2.0) + 100.0 * pow((dZ - dY*dY), 2.0);

	dRosenbrock4D *= -1.0;

	return dRosenbrock4D;
}

double ES_Fitness_Function(genome_t *genome)
/* This is a wrapper for the fitness function you really want to use.  All other
routines call THIS one... you should use it to encapsulate the thing you
actually want to maximize.  As it's written below, this will be a maximizer
of 1's in the genome... */

{
	return ES_Rosenbrock_4D(genome);
}

void ES_Population_Compute_Fitness(population_t *population)
/* This function goes through a whole population and assigns a
fitness score to each genome.  The fitness scores are stored
in each genome structure */

{
	int m_count;

	for (m_count = 0; m_count < (*population).member_count; m_count++)
		((*population).member[m_count]).fitness = ES_Fitness_Function(&((*population).member[m_count]));

}


/************************************************************/

int main()
{

	/* We're going to implement the ES right here in the main loop of the
	program.  For this demo, we're optimizing rosenbrock 2D */

	/* Here's some utility variables. */

	RNG *random_number_generator;
	population_t PARENT_POP, CHILD_POP, NEW_POP;
	int generation_count;
	double champ_score;

	/* Initialize random number generator.  This RNG will be used for all calls where
	random numbers are needed */

	random_number_generator = rng_create();

	/* Allocate memory for three populations... */
	ES_Population_Malloc(&PARENT_POP, 10, 4);    // Set the size of the mu population to 10
	ES_Population_Malloc(&CHILD_POP, 1000, 4);   // Set the size of the lambda population to 1000

	/* Initialize the initial parent population to random values */
	ES_Population_Init(&PARENT_POP, -100.0, 100.0, random_number_generator);
	ES_Population_Init(&CHILD_POP, -100.0, 100.0, random_number_generator);

	/* Begin a simplistic fixed size loop of evolution... it's just a count people ;) */

	for (generation_count = 0; generation_count < 1000; generation_count++)
	{ // Generate the lambda population using uniform random selection of parents into children slots
		ES_Population_Select_Copy(&PARENT_POP, &CHILD_POP, random_number_generator);

		// Mutate members of the child population (lambda population).  I'm not using crossover,
		// but YOU could write an ES_Population_Crossover() routine and insert that right after
		// the mutation function if you so chose.

		ES_Population_Mutate(&CHILD_POP, random_number_generator);

		// Run through and compute the fitnesses of all those kids in the lambda pool
		ES_Population_Compute_Fitness(&CHILD_POP);

		// Do survivor selection by copying the best of the lambda population back into the 
		// mu (parent) population.  Note that ES_Popultion_Best_Copy() automatically determines
		// the size of the mu (parent) population by examination of the structure and will
		// do the right thing.  It returns the score of the best individual in the new 
		// detination population (PARENT_POP).  In this code, though, I'm not really doing much with that
		// value (grin).  I guess I am printing out a generation vs. best score thing.... ;)

		champ_score = ES_Population_Best_Copy(&CHILD_POP, &PARENT_POP);
		printf("%d %lf\n", generation_count, champ_score);
	}

	ES_Population_Print(&PARENT_POP);  // Prints out every member of the population, then skips a line and
	// prints THE BEST member.
	rng_destroy(random_number_generator);
	ES_Population_Free(&PARENT_POP);
	ES_Population_Free(&CHILD_POP);
	return 0;

}
