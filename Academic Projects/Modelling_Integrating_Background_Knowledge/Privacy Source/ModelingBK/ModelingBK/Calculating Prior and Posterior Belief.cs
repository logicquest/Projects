using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace ModelingBK
{
    public partial class Calculating_Prior_and_Posterior_Belief : Form
    {
        public Calculating_Prior_and_Posterior_Belief()
        {
            InitializeComponent();
        }
        Utility util;
        List<String> qi, si;
        private void Calculating_Prior_and_Posterior_Belief_Load(object sender, EventArgs e)
        {
            try
            {
                util = new Utility();

                util.connection_params();
                qi = util.getQI();
                si = util.getSI();
                float bwidth = 0.3f;
                //  Dictionary<String, Dictionary<String, float>> ods = util. rods(qi, si);
                util.pb = util.pbfunc(bwidth, qi, si);



                foreach (KeyValuePair<String, Dictionary<String, float>> entry in util.pb)
                {
                    // do something with entry.Value or entry.Key

                    foreach (KeyValuePair<String, float> val_prob in entry.Value)
                    {
                        listBox1.Items.Add(entry.Key + "--------->" + val_prob.Key + "   " + val_prob.Value);
                    }


                }

                util.myCon.Close();

                PostBeliefUtil pbu = new PostBeliefUtil();
                List<String> new_qi = pbu.getQIs();
                //Dictionary<string, List<String>> t_sis;
                Dictionary<string, List<String>> t_sis = new Dictionary<string, List<string>>();
                Dictionary<string, List<String>> t_tup = new Dictionary<string, List<string>>();

                Dictionary<string, Dictionary<String, Dictionary<String, float>>> final_pos = new Dictionary<string, Dictionary<string, Dictionary<string, float>>>();

                foreach (String qis in new_qi)
                {
                    t_sis.Add(qis, pbu.getSat(qis));
                    t_tup.Add(qis, pbu.getTuples(qis));
                }


                foreach (String qis in new_qi)
                {


                    final_pos.Add(qis, util.post_belief(t_sis[qis], t_tup[qis]));




                }
                listBox2.Items.Clear();

                //                                QI                       SI
                //Dictionary<string, Dictionary<String, Dictionary<String, float>>>

                foreach (String i in final_pos.Keys)
                {

                    foreach (String j in final_pos[i].Keys)
                    {

                        foreach (String k in final_pos[i][j].Keys)
                        {

                            listBox2.Items.Add(i + "--" + j + "--" + k + "--" + final_pos[i][j][k]);
                        }


                    }
                }
                util.myCon.Close();


            }
            catch (Exception e1)
            {
                MessageBox.Show(e1.ToString());


            }
            
            // = pbu.getSat(new_qi);


            /*   util.posb = util.post_belief(si, qi);

               foreach (KeyValuePair<String, Dictionary<String, float>> entry in util.posb)
               {
                   // do something with entry.Value or entry.Key

                   foreach (KeyValuePair<String, float> val_prob in entry.Value)
                   {
                       listBox2.Items.Add(entry.Key + "--------->" + val_prob.Key + "   " + val_prob.Value);
                   }


               }


               */

        }

        private void label1_Click(object sender, EventArgs e)
        {

        }

        private void textBox1_TextChanged(object sender, EventArgs e)
        {

        }

        private void button1_Click(object sender, EventArgs e)
        {

            listBox1.Items.Clear();
            try
            {
                float f = float.Parse(textBox1.Text);
                if (f >= 0.0f && f <= 1.0f) {

                    util.pb = util.pbfunc(f, qi, si);



                    foreach (KeyValuePair<String, Dictionary<String, float>> entry in util.pb)
                    {
                        // do something with entry.Value or entry.Key

                        foreach (KeyValuePair<String, float> val_prob in entry.Value)
                        {
                            listBox1.Items.Add(entry.Key + "--------->" + val_prob.Key + "   " + val_prob.Value);
                        }


                    }

                    util.myCon.Close();

                    PostBeliefUtil pbu = new PostBeliefUtil();
                    List<String> new_qi = pbu.getQIs();
                    //Dictionary<string, List<String>> t_sis;
                    Dictionary<string, List<String>> t_sis = new Dictionary<string, List<string>>();
                    Dictionary<string, List<String>> t_tup = new Dictionary<string, List<string>>();

                    Dictionary<string, Dictionary<String, Dictionary<String, float>>> final_pos = new Dictionary<string, Dictionary<string, Dictionary<string, float>>>();

                    foreach (String qis in new_qi)
                    {
                        t_sis.Add(qis, pbu.getSat(qis));
                        t_tup.Add(qis, pbu.getTuples(qis));
                    }


                    foreach (String qis in new_qi)
                    {


                        final_pos.Add(qis, util.post_belief(t_sis[qis], t_tup[qis]));




                    }
                    listBox2.Items.Clear();

                    //                                QI                       SI
                    //Dictionary<string, Dictionary<String, Dictionary<String, float>>>

                    foreach (String i in final_pos.Keys)
                    {

                        foreach (String j in final_pos[i].Keys)
                        {

                            foreach (String k in final_pos[i][j].Keys)
                            {

                                listBox2.Items.Add(i + "--" + j + "--" + k + "--" + final_pos[i][j][k]);
                            }


                        }
                    }

                    util.myCon.Close();

            }
                else
                {
                    MessageBox.Show("Range Not Accepted");
                }


            }catch(Exception e1)
            {
                MessageBox.Show("Invalid Input");
            }

            

        }

        private void button2_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }
    }
}
