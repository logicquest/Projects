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
    public partial class Gen_Facts : Form
    {
        public Gen_Facts()
        {
            InitializeComponent();
        }
        MineData util = new MineData();
        List<String> qi, si;

        private void Gen_Facts_Load(object sender, EventArgs e)
        {
            
            util.connection_params();
             qi = util.getQI();
             si = util.getSI();
            Dictionary<String, Dictionary<String, float>> min_set = util.rods(qi, si, 0.0f);
            foreach (KeyValuePair<String, Dictionary<String, float>> entry in min_set)
            {
                // do something with entry.Value or entry.Key

                foreach (KeyValuePair<String, float> val_prob in entry.Value)
                {
                    listBox1.Items.Add(entry.Key + "--------->" + val_prob.Key+"   "+val_prob.Value);
                }


            }
            util.myCon.Close();
            
        }

        private void button2_Click(object sender, EventArgs e)
        {
            Calculating_Prior_and_Posterior_Belief cppos = new Calculating_Prior_and_Posterior_Belief();
            cppos.Show();
            this.Hide();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            try
            {
                String s = "0.0";
                float f = float.Parse(s);
                listBox1.Items.Clear();
                if (f >= 0.0f && f <= 0.2f)
                {
                    Dictionary<String, Dictionary<String, float>> min_set = util.rods(qi, si, f);
                    foreach (KeyValuePair<String, Dictionary<String, float>> entry in min_set)
                    {
                        // do something with entry.Value or entry.Key

                        foreach (KeyValuePair<String, float> val_prob in entry.Value)
                        {
                            listBox1.Items.Add(entry.Key + "--------->" + val_prob.Key + "   " + val_prob.Value);
                        }


                    }

                }
                else
                    MessageBox.Show("Range 0.0-0.2 is allowed");
            }catch(Exception e1)
            {
                MessageBox.Show("Invalid Input");
            }
            
          

        }
    }
}
