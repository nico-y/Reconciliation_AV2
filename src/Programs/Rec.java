package Programs;

import ReconciliationUtils.Reconciliation;

public class Rec {

	public static void main(String[] args) {

		// F1 F3 F5 F6
		// =====>O=====>O=====>O=====>
		// | ^
		// | F2 F4 |
		// ======>O======

		// Vetor de medidas 
		double[] y = new double[] { 110.5, 60.8, 35.0, 68.9, 38.6, 101.4 };

		// Vetor de variancias 
		double[] v = new double[] { 0.6724, 0.2809, 0.2116, 0.5041, 0.2025, 1.44 };

		// Matriz de restricoes
		double[][] A = new double[][] { { 1, -1, -1, 0, 0, 0 }, { 0, 1, 0, -1, 0, 0 }, { 0, 0, 1, 0, -1, 0 },
				{ 0, 0, 0, 1, 1, -1 } };

		Reconciliation rec = new Reconciliation(y, v, A);
		System.out.println("Y_hat:");
		rec.printMatrix(rec.getReconciledFlow());
	}
}
