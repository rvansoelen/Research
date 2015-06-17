import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Channel {
	
	public List<List<Double>> data;
	public List<List<Double>> fft;
	public List<Double> rawData;
	public HashMap<Boolean, List<List<Double>>> cutfft;
	
	Channel() {
		rawData = new ArrayList<Double>(); //data with high pass filter applied
		data = new ArrayList<List<Double>>(); //windowed data
		fft = new ArrayList<List<Double>>(); //windowed data in frequency domain
		cutfft = new HashMap<Boolean, List<List<Double>>>(); //cut fft data with true false attributes
	}
	
	public void printData() {
		for(List<Double> l: data) {
			for(Double d : l)
			System.out.println(d);
		}
	}
	
};