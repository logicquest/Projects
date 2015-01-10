using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.OleDb;

using System.IO;


namespace ModelingBK


{
    class MineData
    {
        public OleDbConnection myCon;
        OleDbDataReader reader;

        public void connection_params()
        {
            myCon = new OleDbConnection(@"Provider=Microsoft.ACE.OLEDB.12.0;Data Source=" + Directory.GetCurrentDirectory() + "\\Patient.accdb;Persist Security Info=False;");
            myCon.Open();

        }

        public Dictionary<String, Dictionary<String, float>> rods(List<String> qis, List<String> sis, float maxval)
        {
            Dictionary<String, Dictionary<String, float>> ods = new Dictionary<string, Dictionary<string, float>>();


            foreach (String qi in qis)
            {
                Dictionary<String, float> temp_dic = new Dictionary<string, float>();
                foreach (String si in sis)
                {

                    float tempf = find_probability(si, qi);
                    if (tempf <= maxval)
                        temp_dic.Add(si, find_probability(si, qi));

                }

                ods.Add(qi, temp_dic);
            }

            return ods;
        }

        public float find_probability(String sens, String quasi)
        {
            int den = 0, num = 0;

            reader = (new OleDbCommand("select count(*) from Info where Disease='" + sens + "'", myCon)).ExecuteReader();
            if (reader.Read())
                den = (int)reader[0];
            reader.Close();
            reader = (new OleDbCommand("select count(Age) from Info where Disease='" + sens + "' and Sex='" + quasi + "'", myCon)).ExecuteReader();
            if (reader.Read())
                num = (int)reader[0];

            float a = (float)num / (float)den;

            return a;
        }

        public List<String> getQI()
        {
            List<String> sex = new List<string>();

            reader = (new OleDbCommand("select DISTINCT Sex from Info", myCon)).ExecuteReader();
            while (reader.Read())
                sex.Add(reader["Sex"].ToString());
            reader.Close();

            return sex;

        }

        public List<String> getSI()
        {
            List<String> dis = new List<string>();

            reader = (new OleDbCommand("select DISTINCT Disease from Info", myCon)).ExecuteReader();
            while (reader.Read())
                dis.Add(reader["Disease"].ToString());
            
            return dis;
        }


    }
}
