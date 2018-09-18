package com.filipelipan.stepperview

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.util.AttributeSet
import android.view.View

class StepperView : ConstraintLayout {

    val checkViews: MutableList<CheckView> = mutableListOf<CheckView>()
    lateinit var lineView: View
    lateinit var completeLineView: View
    var count: Int = 2
    var currentStep: Int = 0


    lateinit var entries: Array<CharSequence>

    constructor(context: Context) : super(context) {
        init(null, 0)
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.StepperView, 0, 0)

        entries = attr.getTextArray(R.styleable.StepperView_titles)

        count = attr.getInt(R.styleable.StepperView_quantity, 2) - 1

        if (count < 1) {
            count = 1
        }


        createCompleteLine()
        createCheckViews()
        createLines()

        invalidate()

        attr.recycle()
    }

    private fun createCheckViews() {
        for (i in 0..count) {
            val checkView: CheckView = CheckView(context)
            checkView.setText((i + 1).toString())

            val lp = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT)

            checkView.id = 1651651651 + i
            checkView.layoutParams = lp

            addView(checkView, -1)

            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            if (i == 0) {
                constraintSet.connect(checkView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
            } else if (i == count) {
                constraintSet.connect(checkView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
            } else {
                constraintSet.connect(checkView.id, ConstraintSet.START, checkViews.get(i - 1).id, ConstraintSet.END, 0)
            }

            checkViews.add(checkView)
            constraintSet.applyTo(this)
        }

        for (i in 0..count) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            if (!(i == count || i == 0)) {
                constraintSet.connect(checkViews.get(i).id, ConstraintSet.END, checkViews.get(i + 1).id, ConstraintSet.START, 0)
            }

            constraintSet.applyTo(this)
        }
    }

    private fun createLines() {
        val lineView: View = View(context)

        val lineLayoutParams = LayoutParams(
                0,
                6)

        lineView.id = 1751651651

        lineView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray8b99aa))

        lineView.layoutParams = lineLayoutParams

        addView(lineView, 0)

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        constraintSet.connect(lineView.id, ConstraintSet.START,  ConstraintSet.PARENT_ID, ConstraintSet.START, 5)
        constraintSet.connect(lineView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 5)

        constraintSet.connect(lineView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        constraintSet.connect(lineView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)

        constraintSet.connect(completeLineView.id, ConstraintSet.END, checkViews[0].id, ConstraintSet.END, 5)
        constraintSet.connect(completeLineView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 5)

        constraintSet.applyTo(this)
        this.lineView = lineView

    }



    private fun createCompleteLine() {
        val completeLineView = View(context)

        val lineLayoutParams = LayoutParams(
                0,
                6)

        completeLineView.id = 1756131651

        completeLineView.setBackgroundColor(ContextCompat.getColor(context, R.color.green009ca1))

        completeLineView.layoutParams = lineLayoutParams

        addView(completeLineView, -1)

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        constraintSet.connect(completeLineView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 5)
        constraintSet.connect(completeLineView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)

        constraintSet.connect(completeLineView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        constraintSet.connect(completeLineView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)

        constraintSet.applyTo(this)

        this.completeLineView = completeLineView
    }

    fun goToNextStep() {

        if (checkViews.get(currentStep).isChecked()) {

            if (isInitialStep()) {
                checkViews.get(currentStep).markAsFinished()
                incrementCurrentStep()
            } else {
                val constraintSet = ConstraintSet()
                constraintSet.clone(this)
                constraintSet.connect(completeLineView.id, ConstraintSet.END, checkViews.get(currentStep).id, ConstraintSet.END, 5)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(this, getCompleteLineTransition(checkViews.get(currentStep)))
                } else {
                    checkViews.get(currentStep).markAsFinished()
                    incrementCurrentStep()
                }
                constraintSet.applyTo(this)
            }
        } else {
            checkViews.get(currentStep).checkButton()
            incrementCurrentStep()
        }

    }

    fun goToPreviousStep() {
        decrementCurrentStep()
        if (checkViews.get(currentStep).isMarkAsFinished()) {

            if (isInitialStep()) {
                checkViews.get(currentStep).checkButton()
            } else {
                val constraintSet = ConstraintSet()
                constraintSet.clone(this)
                constraintSet.connect(completeLineView.id, ConstraintSet.END, checkViews.get(currentStep - 1).id, ConstraintSet.END, 5)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(this, getCompleteLineReverseTransition(checkViews.get(currentStep)))
                    checkViews.get(currentStep).checkButton()
                } else {
                    checkViews.get(currentStep).checkButton()
                }
                constraintSet.applyTo(this)
            }
        } else {
            checkViews.get(currentStep).unCheckButton()
        }
    }

    private fun isInitialStep(): Boolean {
        return currentStep == 0
    }

    private fun isLastStep(): Boolean {
        return currentStep == count
    }

    private fun incrementCurrentStep() {
        if (currentStep < count && checkViews.get(currentStep).isMarkAsFinished()) {
            currentStep++
        }
    }

    private fun decrementCurrentStep() {
        if (currentStep > 0 && checkViews.get(currentStep).unChecked()) {
            currentStep--
        }
    }

    fun resetStepper() {

        checkViews.forEach {
            it.unChecked()
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        constraintSet.connect(completeLineView.id, ConstraintSet.END, checkViews.get(0).id, ConstraintSet.END, 0)

        constraintSet.applyTo(this)
    }


    private fun getCompleteLineTransition(checkView: CheckView): TransitionSet? {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionSet()
                    .addTransition(ChangeBounds().setDuration(200).addTarget(completeLineView.id).addListener(object : Transition.TransitionListener {
                        override fun onTransitionEnd(p0: Transition?) {
                            checkView.markAsFinished()
                            incrementCurrentStep()
                        }

                        override fun onTransitionResume(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionPause(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionCancel(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionStart(p0: Transition?) {
                            //no use
                        }

                    }))
                    .setInterpolator(FastOutSlowInInterpolator())
        } else {
            return null
        };
    }


    private fun getCompleteLineReverseTransition(checkView: CheckView): TransitionSet? {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionSet()
                    .addTransition(ChangeBounds().setDuration(300).addTarget(completeLineView.id).addListener(object : Transition.TransitionListener {
                        override fun onTransitionEnd(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionResume(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionPause(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionCancel(p0: Transition?) {
                            //no use
                        }

                        override fun onTransitionStart(p0: Transition?) {
                            checkView.checkButton()
                        }

                    }))
                    .setInterpolator(FastOutSlowInInterpolator())
        } else {
            return null
        };
    }


    public override fun onSaveInstanceState(): Parcelable? {
        //begin boilerplate code that allows parent classes to save state
        val superState = super.onSaveInstanceState()

        val ss = SavedState(superState)
        //end

        ss.currentStep = this.currentStep


        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        //begin boilerplate code so parent classes can restore state
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        //end

        this.currentStep = state.currentStep

        recoverState(currentStep = currentStep)
    }

    private fun recoverState(currentStep: Int = 0) {

        if (currentStep > 0) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            constraintSet.connect(completeLineView.id, ConstraintSet.END, checkViews.get(currentStep - 1).id, ConstraintSet.END, 5)

            constraintSet.applyTo(this)
        }
    }

    internal class SavedState : View.BaseSavedState {
        var currentStep: Int = 0
        var stepStatus: Int = 0

        constructor(superState: Parcelable) : super(superState) {}

        private constructor(inParcelable: Parcel) : super(inParcelable) {
            this.currentStep = inParcelable.readInt()
            this.stepStatus = inParcelable.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)

            out.writeInt(currentStep)
            out.writeInt(stepStatus)
        }


        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

    }
}
