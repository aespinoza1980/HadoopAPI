# HadoopAPI
Prerequisites:
Install and configure Hadoop(Version used Hadoop 1.2.1). http://hadoop.apache.org/releases.html

After Hadoop is succesfully installed,  go to the command line and start hadoop.

1) sudo start-all.sh


2) Create a directory structure in hadoop exactly like this one:

   hadoop fs -mkdir /user/hadoop/project3/datasets
   hadoop fs -mkdir /user/hadoop/project3/output

3) Upload a dataset(Unstructured dataset(Ricardo.txt, WordCount.txt) Or structured Multidimensional dataset(AccessLogs.txt), all of them  provided in the project in the SampleDS folder. You can  always upload your own)

hadoop fs -put WordCount.txt /user/hadoop/project3/datasets

4) Open the project with your favorite IDE

5) Open the program CmdLineParser.java.  Compile it and run it with any of the following  arguments
 
Count DATASET <name> COLUMN <number>

TopX <integer> DATASET <name> COLUMN <number>

Search <value> DATASET <name> COLUMN <number>


TopX  10 DATASET AccessLogs.txt COLUMN 3

Count DATASET AccessLogs.txt COLUMN 4

Search "justViewed" DATASET AccessLogs.txt COLUMN 4


6) Open  firefox and go to Bookmarks on the top.

7) Click on Hadoop NameNode-> Browse the filesystem->user->hadoop->project3
   The folder datasets contains all the datasets uploaded. The  folder output contains all the outputs as the program is      executed everytime.



