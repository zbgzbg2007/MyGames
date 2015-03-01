import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class FrequentWord {

        public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{

                private final static IntWritable one = new IntWritable(1);
                private Text word = new Text();

                public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
                        StringTokenizer itr = new StringTokenizer(value.toString());
                        while (itr.hasMoreTokens()) {
                                word.set(itr.nextToken());
                                context.write(word, one);
                        }
                }
        }

        public static class IntSumReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
                private IntWritable result = new IntWritable();

                public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
                        int sum = 0;
                        for (IntWritable val : values) {
                                sum += val.get();
                        }
                        result.set(sum);
                        context.write(key, result);
                }
        }

        public static void main(String[] args) throws Exception {
                Configuration conf = new Configuration();
                Job job = Job.getInstance(conf, "Frequent Word");
                job.setJarByClass(FrequentWord.class);
                job.setMapperClass(TokenizerMapper.class);
                job.setCombinerClass(IntSumReducer.class);
                job.setReducerClass(IntSumReducer.class);
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(IntWritable.class);
                FileInputFormat.addInputPath(job, new Path(args[0]));
                FileOutputFormat.setOutputPath(job, new Path(args[1]));
                job.waitForCompletion(true);


                String srcpath = args[1];
                Configuration confg = new Configuration();
                FileSystem hdfs = FileSystem.get(new Configuration());
                FileStatus[] status = hdfs.listStatus(new Path(args[1]));

                InputStreamReader isr = null;
                BufferedReader br = null;
                String result = "";
                long count;
                long max = -1;
                for (FileStatus file: status)
                try {
                        isr = new InputStreamReader(hdfs.open(file.getPath()));
                        br = new BufferedReader(isr);
                        String line = "";
                        String word;
                        while ((line = br.readLine()) != null) {
                                int index = line.lastIndexOf(9);
                                word = line.substring(0,index);
                                count = Long.parseLong(line.substring(index+1));
                                if (count > max) {
                                        result = word;
                                        max = count;
                                }
                        }
                        isr.close();
                        br.close();
                }
                catch (IOException e) {
                        System.out.println(e);
                }
                System.out.println(result);
        }
}
