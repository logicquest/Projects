********************************************************Read Me*********************************************************

Extract the zip file 


**Prerequisite -
	MSAccess Db(2010) is required for viewing and executing the program
	Please Change your antivirus settings to allow all programs to run this program

***Help	
	Please e-mail to hariharan.11@wright.edu if any constrains are faced while running this program
	

	
**DataSet
	Patient.accdb
	
**Application File
	Double click the ModelingBK application file.(Please select allow usage any changes in-case of anti-virus issues)

**Source Folder
	The code is written on C# . NET. The entire code source is posted in Source folder/
	
	Please install visual studio 2010 or above to open the ModelingBk.sln file.
	
	Classes are present in ModelingBk folder
	1. Utility.cs
	2. PostBeliefUtil.cs
	3. MineData.cs
	4. GenFacts.cs
	5. Calculating Prior and Posterior Belief.cs
	
	are the list of classes used in this program
	
***Running Instructions

Step 1: Please install Access Db 2010(**Tested on 2010 not sure with previous versions).. If present proceed to step2

Step 2: Double click the ModelingBK application file.(Please select allow usage any changes in-case of anti-virus issues)

Step 3: Press Launch Button to launch the application

Step 4: Facts from the dataset( Patient.accdb) are mined using negative association rules and displayed. Press Next to view (B,t) Privacy Model

Step 5: Prior and Posterior Belief are calculated using Kernel Estimation and Approximation Inference Techniques
	(Clear Results are Obtained when Bandwidth = 0.1)

Step 6: Application Closes only if Exit Key is pressed, else will be running as background service.

	
****************************Complete**********************************************************************************************************	



	