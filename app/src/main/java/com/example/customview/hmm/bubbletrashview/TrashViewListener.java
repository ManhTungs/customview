package com.example.customview.hmm.bubbletrashview;


/**
 * TrashViewA listener that handles the events of .
 * INFO: Due to the specification that the delete icon follows,
 *       the end of the OPEN animation is not notified.
 */
interface TrashViewListener {

    /**
     * Require ActionTrashIcon updates.
     */
    void onUpdateActionTrashIcon();

    /**
     * Notified when an animation has started.
     *
     * @param animationCode animation code
     */
    void onTrashAnimationStarted(@BubbleTrash.AnimationState int animationCode);

    /**
     * Notified when the animation has finished.
     *
     * @param animationCode animation code
     */
    void onTrashAnimationEnd(@BubbleTrash.AnimationState int animationCode);


}
