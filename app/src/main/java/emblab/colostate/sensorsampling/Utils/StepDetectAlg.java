package emblab.colostate.sensorsampling.Utils;

/**
 * Created by Zemel on 3/16/17.
 */

public class StepDetectAlg {

    private double SampRate = 10000;
    private double mu_alpha;
    private double ro;
    private AccSignal a_p;
    private AccSignal a_v;
    private double alpha = 0.4;
    public AccSignal a[];
    private double th_p = 1 / 3 * SampRate;


    //s_c: signal candidate
    public void stepDetection(AccSignal a_n, AccSignal a_nm1, AccSignal a_np1, int count) {

        SType s_c = detectCandidate(a_nm1, a_n, a_np1, 0.4, 0.4, alpha);
        if (s_c == SType.PEAK) {
            if (a_nm1.getType() == SType.INIT) {
                a_n.setType(SType.PEAK);
                updatePeak(a_n, a_n.index);
            } else if (a_nm1.getType() == SType.VALLEY && (a_n.index - a_nm1.index) > th_p) {
                a_n.setType(SType.PEAK);
                updatePeak(a_n, a_n.index);
                updateStepAverage();
            }
        } else if (s_c == SType.VALLEY) {
            //if (a_nm1.getType()==SType.){}
        }
    }

    //Th_p = mu_p - sigma_p/beta
    //Th_v = mu_p - sigma_v/beta
    public SType detectCandidate(AccSignal a_nm1, AccSignal a_n, AccSignal a_np1, double mu_alpha, double ro_alpha, double alpha) {
        SType s_c = SType.INTMD;
        if (a_n.getMagnitude() > max(a_nm1.getMagnitude(), a_n.getMagnitude(), mu_alpha + ro_alpha / alpha)) {
            s_c = SType.PEAK;
        } else if (a_n.getMagnitude() < min(a_nm1.getMagnitude(), a_np1.getMagnitude(), mu_alpha - ro_alpha / alpha)) {
            s_c = SType.VALLEY;
        }
        return s_c;
    }

    public void updatePeak(AccSignal a_n, int n) {
        double mu_p = a_n.getMagnitude();
        //ro_p = 1.0;
        //n_p = n;
    }

    public void updateValley(AccSignal a_n, int n) {
        //mu_v = 1.0;
        //ro_v = 1.0;
        //n_v = n;
    }

    private void updateStepAverage() {
        mu_alpha = (a_p.getMagnitude() + a_v.getMagnitude()) / 2;
    }


    private double max(double a, double b, double c) {
        double max_t = Math.max(a, b);
        return Math.max(max_t, c);
    }

    private double min(double a, double b, double c) {
        double min_t = Math.min(a, b);
        return Math.min(min_t, c);
    }

    private double getMup(int begin, int end) {
        double average_sum = 0.0;
        int total_num = end - begin + 1;
        for (int i = begin; i <= end; ++i) {
            average_sum = average_sum + a[i].getMagnitude();
        }
        return average_sum / total_num;
    }

    private double getStandardDeviation() {
        double sd = 0.0;
        for (AccSignal a_i : this.a) {
            sd += Math.pow(a_i.getMagnitude(), 2);
        }
        return Math.sqrt(sd / this.a.length);
    }

    private void getGravity(AccSignal[] acc) {
        double x_sum = 0.0;
        double y_sum = 0.0;
        double z_sum = 0.0;
        for (AccSignal a : acc) {
            //TODO
            x_sum += a.x;
            y_sum += a.y;
            z_sum += a.z;
        }

    }

}
