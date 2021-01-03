package me.mking.currentconditions.presentation.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import me.mking.currentconditions.databinding.CurrentConditionsCardViewBinding

class CurrentConditionsCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var viewBinding: CurrentConditionsCardViewBinding =
        CurrentConditionsCardViewBinding.inflate(LayoutInflater.from(context), this)

    var conditionText: CharSequence
        get() = viewBinding.currentConditionsCardViewCondition.text
        set(value) {
            viewBinding.currentConditionsCardViewCondition.text = value
        }

    var temperatureText: CharSequence
        get() = viewBinding.currentConditionsCardViewTemp.text
        set(value) {
            viewBinding.currentConditionsCardViewTemp.text = value
        }

    var windSpeedText: CharSequence
        get() = viewBinding.currentConditionsCardViewWindSpeed.text
        set(value) {
            viewBinding.currentConditionsCardViewWindSpeed.text = value
        }

    var windDirectionText: CharSequence
        get() = viewBinding.currentConditionsCardViewWindDirection.text
        set(value) {
            viewBinding.currentConditionsCardViewWindDirection.text = value
        }

    var iconSrc
        get() = ""
        set(value) {
            Glide.with(context)
                .load(value)
                .into(viewBinding.currentConditionsCardViewIcon)
        }

}