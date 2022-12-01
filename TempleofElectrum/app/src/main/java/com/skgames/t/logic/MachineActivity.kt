package com.skgames.t.logic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.skgames.t.R
import com.skgames.t.databinding.ActivityMachineBinding
import java.util.*

class MachineActivity : AppCompatActivity() {

    private var winCount = 0
    private var lossCount = 0
    private var almostCount = 0

    private var slotSelection1 = -1
    private var slotSelection2 = -1
    private var slotSelection3 = -1

    private lateinit var binding: ActivityMachineBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_machine)

        binding = ActivityMachineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.gameTextView.text = getString(R.string.spin_message)
        binding.winCountTextView.text = winCount.toString()
        binding.lossCountTextView.text = lossCount.toString()

        binding.slotImageView1.setImageResource(R.drawable.item1)
        binding.slotImageView2.setImageResource(R.drawable.item2)
        binding.slotImageView3.setImageResource(R.drawable.item3)
    }

    private fun delaySpin(times: Int) {
        var times = times
        times--
        val timesTracker = times

        Handler().postDelayed({
            slotSelection1 = Random().nextInt(5)
            slotSelection2 = Random().nextInt(5)
            slotSelection3 = Random().nextInt(5)

            slotSelect(slotSelection1, slotSelection2, slotSelection3)
            if (timesTracker != 0) {
                delaySpin(timesTracker)
            } else {
                gameResults(slotSelection1, slotSelection2, slotSelection3)
            }
        }, 100)
    }

    fun spinSelected(view: View?) {
        slotSelection1 = Random().nextInt(5)
        slotSelection2 = Random().nextInt(5)
        slotSelection3 = Random().nextInt(5)
        slotSelect(slotSelection1, slotSelection2, slotSelection3)
        delaySpin(20)
    }

    private fun gameResults(slotSelection1: Int, slotSelection2: Int, slotSelection3: Int) {
        if (slotSelection1 == slotSelection2 && slotSelection2 == slotSelection3) {
            userWonGame()
        } else if(slotSelection1 == slotSelection2 || slotSelection2 == slotSelection3)
            userWonGame()
        else if (slotSelection1 == slotSelection3) {
            userAlmostGame()
        } else {
            userLostGame()
        }

    }

    private fun userWonGame() {
        binding.gameTextView.text = getString(R.string.win_message)
        winCount++
        binding.winCountTextView.text = winCount.toString()
    }

    private fun userAlmostGame() {
        binding.gameTextView.text = getString(R.string.almost_message)
        almostCount++
        lossCount++
        binding.lossCountTextView.text = lossCount.toString()
    }

    private fun userLostGame() {
        binding.gameTextView.text = getString(R.string.lose_message)
        lossCount++
        binding.lossCountTextView.text = lossCount.toString()
    }

    private fun slotSelect(slotSelection1: Int, slotSelection2: Int, slotSelection3: Int) {
        if (slotSelection1 == 0) {
            binding.slotImageView1.setImageResource(R.drawable.item1)
        }
        if (slotSelection1 == 1) {
            binding.slotImageView1.setImageResource(R.drawable.item2)
        }
        if (slotSelection1 == 2) {
            binding.slotImageView1.setImageResource(R.drawable.item3)
        }
        if (slotSelection1 == 3) {
            binding.slotImageView1.setImageResource(R.drawable.item4)
        }
        if (slotSelection1 == 4) {
            binding.slotImageView1.setImageResource(R.drawable.item5)
        }


        if (slotSelection2 == 0) {
            binding.slotImageView2.setImageResource(R.drawable.item1)
        }
        if (slotSelection2 == 1) {
            binding.slotImageView2.setImageResource(R.drawable.item2)
        }
        if (slotSelection2 == 2) {
            binding.slotImageView2.setImageResource(R.drawable.item3)
        }
        if (slotSelection2 == 3) {
            binding.slotImageView2.setImageResource(R.drawable.item4)
        }
        if (slotSelection2 == 4) {
            binding.slotImageView2.setImageResource(R.drawable.item5)
        }


        if (slotSelection3 == 0) {
            binding.slotImageView3.setImageResource(R.drawable.item1)
        }
        if (slotSelection3 == 1) {
            binding.slotImageView3.setImageResource(R.drawable.item2)
        }
        if (slotSelection3 == 2) {
            binding.slotImageView3.setImageResource(R.drawable.item3)
        }
        if (slotSelection3 == 3) {
            binding.slotImageView3.setImageResource(R.drawable.item4)
        }
        if (slotSelection3 == 4) {
            binding.slotImageView3.setImageResource(R.drawable.item5)
        }
    }

}