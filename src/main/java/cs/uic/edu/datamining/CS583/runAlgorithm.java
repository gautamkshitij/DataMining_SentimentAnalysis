package cs.uic.edu.datamining.CS583;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.*;
import weka.core.converters.LibSVMLoader;
import weka.core.converters.LibSVMSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class runAlgorithm {

	public void run(ArrayList<Tweet> tweet, DocumentProperties dp) throws Exception {

		System.out.println("running analysis...");
		FileWriter fr = new FileWriter(sentiAnalysis.DIR.concat(sentiAnalysis.outout.concat(".libsvm")));

		for (int i = 0; i < tweet.size(); i++) {
			int ind = 1;
			if (tweet.get(i).label.equals("positive"))
				fr.append("1 ");
			else if (tweet.get(i).label.equals("negative"))
				fr.append("-1 ");
			else if (tweet.get(i).label.equals("neutral"))
				fr.append("0 ");
			for (int num : tweet.get(i).unifeatures) {
				fr.append(ind + ":" + num + " ");
				ind++;
			}

			fr.append(String.valueOf(ind++ + ":" + tweet.get(i).prob[0]) + " ");
			fr.append(String.valueOf(ind++ + ":" + tweet.get(i).prob[1]) + " ");
			fr.append(String.valueOf(ind++ + ":" + tweet.get(i).prob[2]) + " ");
			fr.append(String.valueOf(ind++ + ":" + tweet.get(i).aifnnScore) + " ");
			fr.append(String.valueOf(ind++ + ":" + tweet.get(i).sentiScore) + " ");
			fr.append("\n");

		}

		fr.close();

	}

	int lowerBound;
	int upperBound;
	Instances filteredData = null;
	int AttrNo, RecordNo;
	double[][] InfoGain = null; // stores feature indices and respective
								// infogain values
	int[] SelectedAttributes = null;

	public void runFilter() throws Exception {
		System.out.println("filtering attributes...");
		System.out.println("running weka filters and weka-libsvm");
		File svmfile = new File(sentiAnalysis.DIR.concat(sentiAnalysis.outout.concat(".libsvm")));
		LibSVMLoader libl = new LibSVMLoader();
		libl.setFile(svmfile);
		Instances data = libl.getDataSet();

		NumericToNominal nm = new NumericToNominal(); // Converting last index
														// attribute to type
														// nominal from numeric
		nm.setAttributeIndices("last"); // as the last index would be class
										// label for the data
		nm.setInputFormat(data);

		filteredData = Filter.useFilter(data, nm); // filtered data stored in
													// new Instances object

		AttrNo = filteredData.numAttributes(); // number of attributes in given
												// file
		RecordNo = filteredData.numInstances(); // Number of records in given
												// file
		lowerBound = 0;
		upperBound = AttrNo - 1;
		AttributeSelection atsl = new AttributeSelection();
		Ranker search = new Ranker();
		InfoGainAttributeEval infog = new InfoGainAttributeEval(); // Applying
																	// Attribute
																	// Selection
																	// using
																	// InfoGain
																	// evaluator
																	// with
																	// Ranker
																	// search
		atsl.setEvaluator(infog);
		atsl.setSearch(search);
		atsl.SelectAttributes(filteredData);
		InfoGain = atsl.rankedAttributes();
		SelectedAttributes = atsl.selectedAttributes();

		// count non zero infoGain
		int count = 0;
		for (int i = 0; i < InfoGain.length; i++) {
			count = (InfoGain[i][1] > 0) ? count + 1 : count;
		}

		System.out.println("writing attributes with non-zero InfoGain...");
		FileWriter svmout = new FileWriter(sentiAnalysis.DIR.concat(sentiAnalysis.outout.concat("_new.libsvm")));

		for (int i = 0; i < RecordNo; i++) {
			int index = 1;
			svmout.write((int) filteredData.instance(i).value(filteredData.classIndex()) + " ");
			for (int j = 0; j < count; j++) {
				svmout.write(index + ":" + (int) filteredData.instance(i).value((int) InfoGain[j][0]) + " ");
				index++;
			}
			svmout.write("\n");

		}
		svmout.close();

		// filtered
		File newsvm = new File(sentiAnalysis.DIR.concat(sentiAnalysis.outout.concat("_new.libsvm")));
		LibSVMLoader liblnew = new LibSVMLoader();
		liblnew.setFile(newsvm);
		Instances newdata = liblnew.getDataSet();
		nm = new NumericToNominal(); // Converting last index attribute to type
										// nominal from numeric
		nm.setAttributeIndices("last"); // as the last index would be class
										// label for the data
		nm.setInputFormat(newdata);
		Instances filteredDataNew = Filter.useFilter(newdata, nm); // filtered
																	// data
																	// stored in
																	// new
																	// Instances
																	// object

		// test file
		File newsvmtest = new File(sentiAnalysis.DIR.concat(sentiAnalysis.outout.concat("_test.libsvm")));
		LibSVMLoader libltest = new LibSVMLoader();
		libltest.setFile(newsvmtest);
		Instances newdatatest = libltest.getDataSet();
		nm = new NumericToNominal(); // Converting last index attribute to type
										// nominal from numeric
		nm.setAttributeIndices("last"); // as the last index would be class
										// label for the data
		nm.setInputFormat(newdatatest);
		Instances filteredDataTest = Filter.useFilter(newdatatest, nm); // filtered
																		// data
																		// stored
																		// in
																		// new
																		// Instances
																		// object

		// weka.classifiers.functions.LibSVM -S 0 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5
		// -M 40.0 -C 1.0 -E 0.001 -P 0.1 -seed 1
		String[] options = new String[1];
		options[0] = "-S 0 -K 2 -D 3 -G 0.1 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -seed 1 -h 0";
		System.out.println("building classifier...");
		LibSVM svm_model = new LibSVM();
		svm_model.setOptions(options); // set the options
		svm_model.buildClassifier(filteredData); // build classifier

		DecimalFormat df = new DecimalFormat("0.00");

		System.out.println("running cross validation...");
		Evaluation eval = new Evaluation(filteredData);
		// eval.crossValidateModel(svm_model, filteredDataNew, 10, new
		// Random(1));
		eval.evaluateModel(svm_model, filteredDataTest);

		FileWriter results = new FileWriter(sentiAnalysis.DIR.concat(sentiAnalysis.outout.concat("_results.txt")));

		results.write("Classifier 1: Support Vector Machines\n");
		results.write("Positive class precision: " + df.format(eval.precision(0)) + "\n");
		results.write("Positive class recall: " + df.format(eval.recall(0)) + "\n");
		results.write("Positive class f-score: " + df.format(eval.fMeasure(0)) + "\n");
		results.write("Negative class precision: " + df.format(eval.precision(0)) + "\n");
		results.write("Negative class recall: " + df.format(eval.precision(0)) + "\n");
		results.write("Negative class f-score: " + df.format(eval.fMeasure(0)) + "\n");

		System.out.println("generating results...");
		System.out.println("*" + sentiAnalysis.outout + "*\t" + "\tPositive\tNegative\tNeutral");
		System.out.println("Precision\t" + df.format(eval.precision(0)) + "\t" + df.format(eval.precision(2)) + "\t"
				+ df.format(eval.precision(1)));
		System.out.println("Recall\t" + df.format(eval.recall(0)) + "\t" + df.format(eval.recall(2)) + "\t"
				+ df.format(eval.recall(1)));
		System.out.println("F-score\t" + df.format(eval.fMeasure(0)) + "\t" + df.format(eval.fMeasure(2)) + "\t"
				+ df.format(eval.fMeasure(1)));

		results.close();
	}

}
