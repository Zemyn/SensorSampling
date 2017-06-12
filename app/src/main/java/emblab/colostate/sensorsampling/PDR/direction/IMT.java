package emblab.colostate.sensorsampling.PDR.direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Zemel on 5/5/17.
 */

public class IMT {
    private static final float MAGNITUDE_THRESHOD = 0.0f;
    private static final float REGULAR_THRESHOD = 3.0f;

    public IMT(){}

    public float[][] vectorSelection(Queue<float[]> input){
        List l = new ArrayList(input);

        float[] s_1 = (float[]) l.get(0);
        float[] s_2 = (float[]) l.get(1);
        float[] s_3 = (float[]) l.get(2);

        if(!checkParallel(s_1,s_2) && !checkParallel(s_1,s_3) && !checkParallel(s_2,s_3)){
            return new float[][] {s_1,s_2,s_3};
        }
        return null;
    }

    /**
     * @param v_1 the first vector
     * @param v_2 the second vector
     * @return v_1 and v_2 is parallel to each other or not
     */
    private boolean checkParallel(float[] v_1,float[] v_2){
        float delta_x = v_1[0]/v_2[0];
        float delta_y = v_1[1]/v_2[1];
        return (delta_x - delta_y) < 0.00001;
    }

    /**
     * let the vector intersect in one point
     * @param tri (2D vector)
     * @param distance （2D vector）
     */
    public float[] getMagnetVector(float[][] tri, float[] distance){
        float[] v_1 = tri[0];
        float[] v_2 = tri[1];
        float[] v_3 = tri[2];

        /*  the initial magnitude is set as the measured magnitude
         *  here use the magnitude of vector v_1
         */
        float m = (float)Math.sqrt(v_1[0]*v_1[0]+v_1[1]*v_1[1]);

        float[] dir = normalize(distance); //get the direction

        // the magnetic signal equals to the direction multiplies the magnitude
        float[] raw_meg = {dir[0]*m,dir[1]*m};
        float[] meg = raw_meg;
        int angle = 0;
        while(m > MAGNITUDE_THRESHOD){//todo
            float[][][] tri_intersection_points = new float[360][][];
            for(int i=0; i<360; i++){
                double[][] r_matrix = build2DRotateMatrix(i);
                meg = getRotated2DVector(r_matrix, raw_meg);

                // get the three R vectors
                float[] r1 = vectorMinus(v_1,meg);
                float[] r2 = vectorMinus(v_2,meg);
                float[] r3 = vectorMinus(v_3,meg);

                //get the intersection points
                tri_intersection_points[i] = getInterSectionPoints(r1,r2,r3,distance);
            }
            if(checkMagnitudeValidation(tri_intersection_points)){
                angle = getBestAngle(tri_intersection_points);
                break;
            }else{
                m--;
                meg[0] = dir[0]*m;
                meg[1] = dir[1]*m;
            }
        }

        return getRotated2DVector(build2DRotateMatrix(angle),meg);
    }

    private int getBestAngle(float[][][] tri_pair_points){
        float min_cluster = Float.MAX_VALUE;
        int min_angle = 0,angle = 0;
        while(angle < 360){
            float dis12 = getDis(tri_pair_points[angle][0],tri_pair_points[angle][1]);
            float dis13 = getDis(tri_pair_points[angle][0],tri_pair_points[angle][1]);
            float dis23 = getDis(tri_pair_points[angle][0],tri_pair_points[angle][1]);
            float cluster = dis12+dis13+dis23;
            if (cluster<min_cluster){
                min_cluster = cluster;
                min_angle = angle;
            }
            angle++;
        }
        return min_angle;
    }

    private boolean checkMagnitudeValidation(float[][][] tri_points){
        return regularShape(tri_points);
    }

    /**
     * the method to detect the
     * @param points the list represent the locus points
     * @return whether the shape of the locus is regular or not
     */
    private boolean regularShape(float[][][] points){
        float sum_p1 = 0,sum_p2 = 0,sum_p3 = 0;

        //point[360 tri points][p1, p2, p3][x y]
        float max_dis_p1 = getDis(points[0][0],points[359][0]);
        float max_dis_p2 = getDis(points[0][1],points[359][1]);
        float max_dis_p3 = getDis(points[0][2],points[359][2]);

        for(int i=0 ; i<359;i++){
            float dis_p1 = getDis(points[i][0],points[i+1][0]);
            float dis_p2 = getDis(points[i][1],points[i+1][1]);
            float dis_p3 = getDis(points[i][2],points[i+1][2]);

            sum_p1 += dis_p1;
            sum_p2 += dis_p2;
            sum_p3 += dis_p3;

            if(max_dis_p1< dis_p1){ max_dis_p1 = dis_p1; }
            if(max_dis_p2< dis_p2){ max_dis_p2 = dis_p2; }
            if(max_dis_p3< dis_p3){ max_dis_p3 = dis_p3; }
        }

        float avg_p1 = sum_p1/360;
        float avg_p2 = sum_p2/360;
        float avg_p3 = sum_p3/360;

        return !(max_dis_p1 > REGULAR_THRESHOD * avg_p1 || max_dis_p2 > REGULAR_THRESHOD * avg_p2 || max_dis_p3 > REGULAR_THRESHOD * avg_p3);

    }

    private float getDis(float[] a,float[] b){
        return (float)Math.sqrt((a[0]-b[0])*(a[0]-b[0])+(a[1]-b[1])*(a[1]-b[1]));
    }

    /**
     * The cluster is defined as the sum of the distances from three intersection points.
     * First get the slope of each line named k_1 ,k_2, k_3
     * Second get the intersection points as (x12,y12),(x13,y13),(x23,y23)
     *
     * @param a the first vector
     * @param b the second vector
     * @param c the third vector
     * @param dis the distance vector between each vector's original point
     * @return the three intersection points
     */
    private float[][] getInterSectionPoints(float[] a, float[] b, float[] c, float dis[]){
        float k_1 = a[1]/a[0];
        float k_2 = b[1]/b[0];
        float k_3 = c[1]/c[1];

        //calculate the intersection point
        float x12 = (dis[1]-k_2*dis[0])/(k_1-k_2);
        float y12 = (k_1*dis[1]-k_1*k_2*dis[0])/(k_1-k_2);

        float x13 = 2*(dis[1]-k_3*dis[0])/(k_1-k_3);
        float y13 = 2*k_1*(dis[1]-k_3*dis[0])/(k_1-k_3);

        float x23 = (k_2*dis[1]-k_2*k_3*dis[1])/(k_2-k_3);
        float y23 = 2*(k_2*dis[1]-k_2*k_3*dis[0]-k_3*dis[1])/(k_2-k_3);

        //return the sum of 3 distance
        return new float[][] {{x12,y12},{x13,y13},{x23,y23}};
    }


    private float[] vectorMinus(float[] a, float[] b){
        float x = a[0] - b[0];
        float y = a[1] - b[1];
        return new float[] {x,y};
    }

    private float[] normalize(float[] a){
        float mag = (float)Math.sqrt(a[0]*a[0]+a[1]*a[1]);
        return new float[] {a[0]/mag,a[1]/mag};
    }

    /**
     * @param v     the rotate axis vectir v,
     * @param theta the rotate angle theta
     * @return  the 3D rotation matrix
     *         [cosθ+(1-cosθ)x^2    (1-cosθ)xy+(sinθ)z  (1-cosθ)xz+(sinθ)y  ]
     * M(v,θ)= [(1-cosθ)yx+(sinθ)z  cosθ+(1-cosθ)y^2    (1-cosθ)yz+(sinθ)x  ]
     *         [(1-cosθ)zx+(sinθ)y  (1-cosθ)zy+(sinθ)x  cosθ+(1-cosθ)z^2    ]
     */
    private double[][] build3DRotateMatrix(float[] v, int theta){
        double[][] M = new double[3][3];
        M[0][0] = Math.cos(theta)+(1-Math.cos(theta))*v[0]*v[0];
        M[0][1] = (1-Math.cos(theta))*v[0]*v[1]+Math.sin(theta)*v[2];
        M[0][2] = (1-Math.cos(theta))*v[0]*v[2]+Math.sin(theta)*v[1];

        M[1][0] = (1-Math.cos(theta))*v[1]*v[0]+Math.sin(theta)*v[2];
        M[1][1] = Math.cos(theta)+(1-Math.cos(theta))*v[1]*v[1];
        M[1][2] = (1-Math.cos(theta))*v[1]*v[2]+Math.sin(theta)*v[0];

        M[2][0] = (1-Math.cos(theta))*v[2]*v[0]+Math.sin(theta)*v[1];
        M[2][1] = (1-Math.cos(theta))*v[2]*v[1]+Math.sin(theta)*v[0];
        M[2][2] = Math.cos(theta)+(1-Math.cos(theta))*v[2]*v[2];
        return M;
    }

    /**
     * @param theta the rotation angle θ
     * @return the 2D rotation matrix
     *
     * M(v,θ)=  [cosθ   -sinθ]
     *          [sinθ   cosθ]
     */
    private double[][] build2DRotateMatrix(int theta){
        double[][] M = new double[2][2];
        M[0][0] = Math.cos(theta);
        M[0][1] = -Math.sin(theta);

        M[1][0] = Math.sin(theta);
        M[1][1] = Math.cos(theta);

        return M;
    }

    /**
     * @param M the rotation matrix
     * @param v the vector which need to be rotated
     * @return the vector M*v
     */
    private float[] getRotated2DVector(double[][] M, float[] v){
        float x,y;
        x = (float)(M[0][0]*v[0]+M[0][1]*v[1]);
        y = (float)(M[1][0]*v[0]+M[1][1]*v[1]);
        
        return new float[] {x,y};
    }

}
