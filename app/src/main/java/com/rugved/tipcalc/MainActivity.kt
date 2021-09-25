package com.rugved.tipcalc

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val INITIAL_TIP_PERCENT = 15
private const val INITIAL_TOTAL_AMOUNT = 0.0

class MainActivity : AppCompatActivity() {

    private lateinit var etBillAmount: EditText
    private lateinit var seekBarPercent: SeekBar
    private lateinit var tvTipPercent: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipStatus: TextView
    private lateinit var tvSplitAmount: TextView
    private lateinit var etNumberOfPeople: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etBillAmount = findViewById(R.id.etBillAmount)
        seekBarPercent = findViewById(R.id.seekBarTipPercent)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipStatus = findViewById(R.id.tvTipStatus)
        etNumberOfPeople = findViewById(R.id.etNumberPeople)
        tvSplitAmount = findViewById(R.id.tvSplitAmount)

        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        seekBarPercent.progress = INITIAL_TIP_PERCENT
        calculateTip(INITIAL_TIP_PERCENT)
        calculateStatus(INITIAL_TIP_PERCENT)
        calculateSplit(INITIAL_TOTAL_AMOUNT)
        etNumberOfPeople.setText("1")
        seekBarPercent.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvTipPercent.text = "$progress%"
                calculateTip(progress)
                calculateStatus(progress)
                calculateSplit(tvTotalAmount.text.toString().toDouble())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        etBillAmount.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTip(seekBarPercent.progress)
                calculateSplit(tvTotalAmount.text.toString().toDouble())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        etNumberOfPeople.addTextChangedListener(object: TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val total = tvTotalAmount.text.toString().toDouble()
                calculateSplit(total)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun calculateSplit(initialTotalAmount: Double) {
        if(etNumberOfPeople.text.isNotEmpty()){
            val split = etNumberOfPeople.text.toString().toInt()
            val amountPerHead = initialTotalAmount.toDouble() / split
            tvSplitAmount.text = String.format("%.2f", amountPerHead)
        }
    }

    private fun calculateStatus(progress: Int) {
        tvTipStatus.text = when (progress) {
            in 0..9 -> "Bad"
            in 10..14 -> "Average"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Awesome"
        }
        val color = ArgbEvaluator().evaluate(progress.toFloat() / seekBarPercent.max,
            ContextCompat.getColor(this, R.color.worstTip),
            ContextCompat.getColor(this, R.color.bestTip)) as Int
        tvTipStatus.setTextColor(color)
    }

    private fun calculateTip(progress: Int) {
        if (etBillAmount.text.isNotEmpty()) {
            val init: Double = etBillAmount.text.toString().toDouble()
            val result = (init * progress / 100)
            val total = init + result
            tvTipAmount.text = String.format("%.2f", result)
            tvTotalAmount.text = String.format("%.2f", total)
            return
        } else {
            tvTipAmount.text = "0.00"
            tvTotalAmount.text = "0.00"
            return
        }
    }
}