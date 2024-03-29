package ntu.sec.wm.svm;

import java.io.IOException;
import java.util.Vector;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class Train {
	private svm_parameter param;
	private svm_problem prob;
	private svm_model model;
	private String model_file = "wm/model";
	private Vector<float[]> positive;
	private Vector<float[]> negative;

	public Train(Vector<float[]> positive, Vector<float[]> negtive) {
		this.positive = positive;
		this.negative = negtive;
		// System.out.println("Positive "+positive.get(0).length
		// +" Negative "+negative.get(0).length);
	}

	public void run() {
		assign_parameter();
		assign_problem();

		param.gamma = 0.2;
		
//		System.out.println(prob.x.length);
		model = svm.svm_train(prob, param);
		try {
			svm.svm_save_model(model_file, model);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void assign_problem() {
		prob = new svm_problem();
		prob.l = positive.size() + negative.size();
		prob.x = new svm_node[prob.l][];
		prob.y = new double[prob.l];

		for (int i = 0; i < positive.size(); i++) {
			float[] in = positive.get(i);
			svm_node[] x = new svm_node[in.length];
			for (int j = 0; j < x.length; j++) {
				x[j] = new svm_node();
				x[j].index = j + 1;
				x[j].value = in[j];
			}

			prob.x[i] = x;
			prob.y[i] = 1;
		}

		for (int i = 0; i < negative.size(); i++) {
			float[] in = negative.get(i);
			svm_node[] x = new svm_node[in.length];
			for (int j = 0; j < x.length; j++) {
				x[j] = new svm_node();
				x[j].index = j + 1;
				x[j].value = in[j];
			}

			prob.x[i+positive.size()] = x;
			prob.y[i+positive.size()] = 2;
		}
	}

	private void assign_parameter() {
		param = new svm_parameter();
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.degree = 3;
		param.gamma = 0; // 1/num_features
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 100;
		param.C = 100;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
	}
}
