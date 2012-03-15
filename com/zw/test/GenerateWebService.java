package com.zw.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

import com.zw.Configs;
import com.zw.ws.ActivityFlow;

public class GenerateWebService {
	public void generate(int count) {
		String wsinfo = "";
		Random random = new Random();
		DecimalFormat df  = new DecimalFormat("0.00");
		for (int i = 0; i < count; i++) {
			wsinfo += i;
			int price = Math.abs(random.nextInt()) % 1000;
			if (price < 100) {
				price *= 10;
			}
			double realiablity = random.nextDouble() % 1;
			if (realiablity < 0.5) {
				realiablity += 0.5;
			}
			if (realiablity < 0.6) {
				realiablity += 0.1;
			}
			int execTime = Math.abs(random.nextInt()) % 100;
			if (execTime < 50) {
				execTime += 50;
			}
			
			wsinfo += "\t" + price;
			wsinfo += "\t" + df.format(realiablity);
			wsinfo += "\t" + execTime + "\n";
		}
		
		File wsinfoFile = new File(Configs.candidateServiceFileName);
		try {
			FileWriter wsinfoWriter = new FileWriter(wsinfoFile);
			System.out.println("The wsinfo is:\n"+ wsinfo);
			wsinfoWriter.write("[Candidate Service: (ServiceNo, Price, Probability, ExecTime)]\n");
			wsinfoWriter.write(wsinfo);
			wsinfoWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
//		GenerateWebService gen = new GenerateWebService();
//		gen.generate(100);
		ActivityFlow flow = new ActivityFlow();
		flow.printFlow();
		
		//int[][] temp = new int[5][5];
		//System.out.println(temp.length);
	}
}
