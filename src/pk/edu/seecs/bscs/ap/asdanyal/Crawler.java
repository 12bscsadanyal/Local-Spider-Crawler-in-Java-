package pk.edu.seecs.bscs.ap.asdanyal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Crawler {
	static Map<String, String> m1 = new HashMap<String, String>();
	static Map<String, String> m2 = new HashMap<String, String>();
	private me workQ;
	static int i = 0;
	static Scanner in = new Scanner(System.in);
	private class Worker implements Runnable {

		private me queue;

		public Worker(me q) {
			queue = q;
		}

		//  since main thread has placed all directories into the workQ, we
		//  know that all of them are legal directories; therefore, do not need
		//  to try ... catch in the while loop below

		public void run() {
			String name;
			while ((name = queue.remove()) != null) {
				File file = new File(name);
				String entries[] = file.list();
				if (entries == null)
					continue;
				for (String entry : entries) {
					if (entry.compareTo(".") == 0)
						continue;
					if (entry.compareTo("..") == 0)
						continue;
					String fn = name + "\\" + entry;
					// m1.put(entry, fn);

					//     System.out.println("Extension for "+entry+" is "+entry.substring(entry.lastIndexOf('.'),entry.length()));

					if(entry.substring(entry.lastIndexOf('.'),entry.length()).compareTo(".txt")==0 || entry.substring(entry.lastIndexOf('.'),entry.length()).compareTo(".xml")==0||entry.substring(entry.lastIndexOf('.'),entry.length()).compareTo(".doc")==0){
						m1.put(entry, fn);

						String lineContents=null;
						int counter=0;
						try{
							@SuppressWarnings("resource")
							BufferedReader br=new BufferedReader(new FileReader(fn));
							while((lineContents=br.readLine())!=null){
								//lineArray.add(lineContents);
								StringTokenizer st = new StringTokenizer(lineContents," ");
								while (st.hasMoreTokens()) {  
									//temp= st.nextToken();

									m2.put(st.nextToken(), fn);


								}
								counter++;
							}
						}
						catch(FileNotFoundException fne){
							fne.printStackTrace();
						}
						catch(IOException io){
							io.printStackTrace();
						}

					}


					// System.out.println(fn);
				}
			}
		}
	}

	public Crawler() {
		workQ = new me();
	}

	public Worker createWorker() {
		return new Worker(workQ);
	}


	// need try ... catch below in case the directory is not legal

	public void processDirectory(String dir) {
		try{
			File file = new File(dir);
			if (file.isDirectory()) {
				String entries[] = file.list();
				if (entries != null)
					workQ.add(dir);

				for (String entry : entries) {
					String subdir;
					if (entry.compareTo(".") == 0)
						continue;
					if (entry.compareTo("..") == 0)
						continue;
					if (dir.endsWith("\\"))
						subdir = dir+entry;
					else
						subdir = dir+"\\"+entry;
					processDirectory(subdir);
				}
			}}catch(Exception e){}
	}

	public static void main(String Args[]) {

		Crawler fc = new Crawler();

		//  now start all of the worker threads

		int N = 5;
		ArrayList<Thread> thread = new ArrayList<Thread>(N);
		for (int i = 0; i < N; i++) {
			Thread t = new Thread(fc.createWorker());
			thread.add(t);
			t.start();
		}

		System.out.println("Enter the directory name");
		String Directory_Name;
		Directory_Name = in.nextLine();
		//  now place each directory into the workQ
		System.out.println(Directory_Name);
		fc.processDirectory(Directory_Name);

		System.out.println(m1);

		//  indicate that there are no more directories to add

		fc.workQ.finish();

		for (int i = 0; i < N; i++){
			try {
				thread.get(i).join();
			} catch (Exception e) {};
		}
		
		System.out.println(m2);
	}





}
