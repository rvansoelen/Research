import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.lang.Object;


import jfftw.real.Plan;


public class EEGFile {

	
	private static int NUM_CHANNELS = 37;
	private static int WINDOW = 128;
	private static double RC = 1/(6.28318530718*.16);  //seconds
	private static double PERIOD = 1/128.;
	private String filename;
	private Map<String, Channel> channels;
	private String title;
	private String dateRec;  //Date recorded
	private String timeRec;  //Time recorded
	private String subject;
	private int sampleRate; //(Hz)
	String units;   //1 emotiv is nearly equivalent to 1 microvolt
	

	
	EEGFile(String filename) {
			Scanner s;
			try {
				s = new Scanner(new File(filename));
				
				String header = s.nextLine(); 
				s.useDelimiter("\\s*,\\s*|\\s*\\n\\s*");
				channels = new HashMap<String, Channel>();
				//load map with initial stuff
				for(Integer i = 0; i < NUM_CHANNELS; i++ ){
					channels.put(i.toString(), new Channel());
				}
				
				while(s.hasNextLine()) {
					for(Channel c : channels.values()) {
						if(s.hasNext()){							
							c.rawData.add(s.nextDouble());	
						} else {
							System.out.println("Error Reading File");
							System.exit(1);
						}
					}	
				}
				
				 
				for(Channel c : channels.values()) {
					//High pass filter
					System.out.println("Applying high pass filter.");
					c.rawData = HPFilter(c.rawData);
					
					//window function
					System.out.println("Windowing...");
					c.data = WinRect(c.rawData);
					
					//fft
					System.out.println("Applying FFT.");
					c.fft = fourierTransform(c.data);
				}
				
				
				
				
				
				
				s.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	private List<List<Double>> fourierTransform(List<List<Double>> data) {
		List<List<Double>> ret = new ArrayList<List<Double>>();
		int k = 0;
		for(List<Double> dat : data) {
			ret.add(new ArrayList<Double>());
			Double[] dw = dat.toArray(new Double[dat.size()]);
			double[] d = new double[dw.length];
			for(int i = 0; i<dw.length; i++) {
				d[i]=(double)dw[i];
			}
		
			Plan p = new Plan(d.length, Plan.FORWARD);
			d = p.transform(d);
			for(double dee : d) {
				ret.get(k).add(dee);
			}
			k++;
		}
		return ret;
	}
	//rectangular windowing
	private List<List<Double>> WinRect(List<Double> data) {
		List<List<Double>> wind = new ArrayList<List<Double>>();
		wind.add(new ArrayList<Double>());
		int i = 0;
		for(Double d : data){
			if(wind.get(i).size() >= WINDOW){
				wind.add(new ArrayList<Double>());
				i++;
			}
			wind.get(i).add(d);
			
		}
		return wind;
	}

	private List<Double> HPFilter(List<Double> data) {
		List<Double> ret = new ArrayList<Double>();
		double alpha = RC/ (RC + PERIOD);
		ret.add(data.get(0));
		for (int i = 1; i< data.size(); i++) {
			ret.add(alpha * (ret.get(i-1) + data.get(i) - data.get(i-1)));
		}
		
		return ret;
	}

	public void printData() {
		for(Channel c : channels.values()) {
			c.printData();
		}
	}
//	int numChannels();
//	string title();
//	string recDate();
//	string recTime();
//	string subject();
//	int samplingRate();
//	string units();
//	map<stirng, Channel *> channels();
//	Channel * getChannel(string name);
//	string filaname();
	
	public static void main(String[] args) {
		EEGFile eeg = new EEGFile("res/DirectDetection_001.csv");
		System.out.println("Loaded data");
		eeg.printData();
	}
}
