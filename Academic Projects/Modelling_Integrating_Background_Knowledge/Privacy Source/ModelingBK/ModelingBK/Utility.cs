using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.OleDb;
using System.Data;
using System.IO;

namespace ModelingBK
{
      

    class Utility
    {

        public OleDbConnection myCon;
        OleDbDataReader reader;
       public Dictionary<String, Dictionary<String, float>> pb, posb;

        public int calc_frequency(String sens)
        {
            connection_params();
            int freq = 0;
            reader = (new OleDbCommand("select count(*) from Info where Disease='" + sens + "'" , myCon)).ExecuteReader();
            if (reader.Read())
                freq = (int)reader[0];
            myCon.Close();
            
            return freq;
            


        }

        public float summation_qi(List<String> qis, String si)
        {
            float sum = 0.0f;
            foreach (String qi in qis)
            {
                sum = sum + pb[qi][si];
            }
            return sum;
        }

        public float summation_sens(List<String> sis, String qi, List<string> qis)
        {

            float summation = 0.0f;
            foreach (String si in sis)
            {
                int calc_freq = calc_frequency(si);
                float prob_num = pb[qi][si];
                float num = (float)(calc_freq) * prob_num;
                float den = summation_qi(qis, si);
                summation = summation + (num / den);
            }

            return summation;
        }

        public Dictionary<String, Dictionary<String, float>> post_belief(List<String> sis, List<String> qis)
        {
            Dictionary<String, Dictionary<String, float>> ods = new Dictionary<string, Dictionary<string, float>>();
           
            foreach (String si in sis)
            {
                foreach (String qi in qis)
                {
                    int freq = calc_frequency(si);
                    float prob_up = pb[qi][si];
                    float num = (float)freq * prob_up;
                    float den = summation_qi(qis, si);
                    float den_main = summation_sens(sis, qi, qis);
                    if (ods.ContainsKey(qi))
                    {
                        ods[qi].Add(si, ((num) / (den)) / (den_main));
                       
                    }
                    else
                    {
                        Dictionary<string, float> sensrep = new Dictionary<string, float>();
                        sensrep.Add(si, ((num) / (den)) / (den_main));

                        ods.Add(qi, sensrep);
                      
                    }
                }



            }
          
            return ods;
        }


        public void connection_params()
        {
            myCon = new OleDbConnection(@"Provider=Microsoft.ACE.OLEDB.12.0;Data Source="+Directory.GetCurrentDirectory()+"\\Patient.accdb;Persist Security Info=False;");
            myCon.Open();
        }

        public float find_sem_distance(String attr1, String attr2)
        {


            int cas=Math.Abs(int.Parse(attr1)-int.Parse(attr2));
            if(cas==0)
            {
                return 0.0f;
            }
            else
             if(cas<10&&cas>0)
             {
                 return 0.1f;
             }
             else
             if (cas >= 10 && cas < 20)
             {
                 return 0.2f;
             }
             else
                 if (cas >= 20 && cas < 30)
                 {
                     return 0.3f;
                 }
                 else
                     if (cas > 30 && cas < 40)
                     {
                         return 0.4f;
                     }
                     else
                         if (cas > 40 && cas < 50)
                         {
                             return 0.5f;
                         }
                         else
                             if (cas > 50 && cas < 60)
                             {
                                 return 0.6f;
                             }
                             else
                                 if (cas > 60 && cas < 70)
                                 {
                                     return 0.7f;
                                 }
                                 else
                                     if (cas > 70 && cas < 80)
                                     {
                                         return 0.8f;
                                     }
                                     else
                                         if (cas > 80 && cas < 90)
                                         {
                                             return 0.9f;

                                         }
                                         else
                                         {
                                             return 0.0f;
                                         }
                                        
             


            /*
            if (attr1.Equals(attr2))
            {
                return 0.0f;
            }
            else
            {
                return 0.5f;
            }
            */

        }


        public new Dictionary<String, Dictionary<String, float>> pbfunc(float bwidth, List<String> qis, List<String> sis)
        {
            Dictionary<String, Dictionary<String, float>> ods = new Dictionary<string, Dictionary<string, float>>();
           
            foreach (String qi in qis)
            {
                Dictionary<String, float> temp_dic = new Dictionary<string, float>();
                float val = 0.0f;
                foreach (String si in sis)
                {
                    connection_params();
                    float num = 0.0f, den = 0.0f;
                    foreach (String qin in qis)
                    {
                        float sem_dis = find_sem_distance(qi, qin);
                        float k_func = kernel_func(sem_dis, bwidth);
                        float p_sens = find_probability(si, qin);

                        num = num + (p_sens * k_func);
                        den = den + k_func;
                    }
                    temp_dic.Add(si, (num / den));
                    myCon.Close();
                    
                }
                ods.Add(qi, temp_dic);

                //printing 
                
            }
            
            return ods;
        }

        public List<String> getQI()
        {
            List<String> Age = new List<string>();

            reader = (new OleDbCommand("select DISTINCT Age from Info", myCon)).ExecuteReader();
            while (reader.Read())
                Age.Add(reader["Age"].ToString());
            reader.Close();

            return Age;

        }

        public List<String> getSI()
        {
            List<String> dis = new List<string>();

            reader = (new OleDbCommand("select DISTINCT Disease from Info", myCon)).ExecuteReader();
            while (reader.Read())
                dis.Add(reader["Disease"].ToString());
            reader.Close();
            return dis;


        }
        public float pse(List<String> sens, List<String> tuples)
        {

            foreach (String sen in sens)
            {

                foreach (String tuple in tuples)
                {



                }
            }


            return 0.1f;
        }



        public Dictionary<String, Dictionary<String, float>> rods(List<String> qis, List<String> sis,float maxval)
        {
            Dictionary<String, Dictionary<String, float>> ods = new Dictionary<string, Dictionary<string, float>>();


            foreach (String qi in qis)
            {
                Dictionary<String, float> temp_dic = new Dictionary<string, float>();
                foreach (String si in sis)
                {

                    float tempf=find_probability(si, qi);
                    if (tempf<=maxval)
                    temp_dic.Add(si, find_probability(si, qi));

                }

                ods.Add(qi, temp_dic);
            }

            return ods;
        }




        public float kernel_func(float val, float bandwidth)
        {
            float tempval = val / bandwidth;

            if (tempval < 1.0f)
                return (3 / (4 * bandwidth)) * (1 - ((val / bandwidth) * (val / bandwidth)));
            else
                return 0.0f;
        }


        public float find_probability(String sens, String quasi)
        {
            int den = 0, num = 0;

            reader = (new OleDbCommand("select count(*) from Info where Disease='" + sens + "'", myCon)).ExecuteReader();
            if (reader.Read())
                den = (int)reader[0];
            reader.Close();
            reader = (new OleDbCommand("select count(Age) from Info where Disease='" + sens + "' and Age='" + quasi + "'", myCon)).ExecuteReader();
            if (reader.Read())
                num = (int)reader[0];

            float a = (float)num / (float)den;

            return a;
        }
















    }
}
