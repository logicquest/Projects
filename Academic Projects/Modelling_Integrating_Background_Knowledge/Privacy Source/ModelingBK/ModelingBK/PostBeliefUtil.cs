using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.OleDb;
using System.IO;

namespace ModelingBK
{
    class PostBeliefUtil
    {

        private OleDbConnection myCon;
        OleDbDataReader reader;

        public void connection_params()
        {
            myCon = new OleDbConnection(@"Provider=Microsoft.ACE.OLEDB.12.0;Data Source=" + Directory.GetCurrentDirectory() + "\\Patient.accdb;Persist Security Info=False;");
            myCon.Open();
        }

        public List<String> getQIs()
        {
            connection_params();
            List<String> Age = new List<string>();

            reader = (new OleDbCommand("select DISTINCT Age from Info_Anonymized", myCon)).ExecuteReader();
            while (reader.Read())
                Age.Add(reader["Age"].ToString());
            myCon.Close();

            return Age;
        }

        public List<String> getSat(String qi)
        {
            connection_params();
            List<String> disease = new List<string>();

            reader = (new OleDbCommand("select Distinct Disease from Info_Anonymized where Age='"+qi+"'", myCon)).ExecuteReader();
            while (reader.Read())
                disease.Add(reader["Disease"].ToString());
            myCon.Close();

            return disease;



        }
        public List<String> getTuples(String Cond)
        {
            connection_params();
            String[] spl_arr=Cond.Split(',');
            List<String> disease = new List<string>();

            reader = (new OleDbCommand("select Distinct Age from Info where Age>='" + spl_arr[0] + "' and Age<='" + spl_arr[1] + "'", myCon)).ExecuteReader();
            while (reader.Read())
                disease.Add(reader["Age"].ToString());
            myCon.Close();

            return disease;

        }


    }
}
