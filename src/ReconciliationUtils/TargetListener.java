package ReconciliationUtils;

import ThreadElements.MovingTarget;

public interface TargetListener {
    
    /**
     * Ação a ser realizada ao notificar novo alvo 
     */
    public void targetDetected(MovingTarget target);

}
