package com.example.songssam.Fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.songssam.Activitys.ChooseSongActivity
import com.example.songssam.Activitys.GlobalApplication.Companion.prefs
import com.example.songssam.Activitys.MainActivity
import com.example.songssam.Activitys.RecordingActivity
import com.example.songssam.databinding.FragmentUserBinding

/**
 * A simple [Fragment] subclass.
 */
class UserFragment : Fragment() {

    lateinit var binding: FragmentUserBinding

    private val mainActivity: MainActivity by lazy {
        context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        initInfo()
        initChooseSongBTN()
        initRecordingBTN()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initChooseSongBTN() {
        binding.favoriteSongContainer.setOnClickListener {
            val intent = Intent(getActivity(), ChooseSongActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecordingBTN() {
        binding.recordContainer.setOnClickListener {
            val intent = Intent(getActivity(), RecordingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initInfo() {
        if(prefs.getString("profile","").equals("").not())
            Glide.with(binding.profileImage).load(prefs.getString("profile","")).into(binding.profileImage)
        SetIntroText()
    }

    private fun SetIntroText() {
        val nickname = prefs.getString("nickname","익명")
        val originalText = "반갑습니다, $nickname 님 😊 >"

        // SpannableString 생성
        val spannableString = SpannableString(originalText)

        // "nickname" 부분을 bold체 변경 및 하이라이트
        val startIndex = originalText.indexOf(nickname)
        val endIndex = startIndex + nickname.length
        val styleSpan = StyleSpan(Typeface.BOLD)
        val highlightColor = Color.YELLOW // 원하는 하이라이트 색상으로 변경
        val highlightSpan = BackgroundColorSpan(highlightColor)

        spannableString.setSpan(styleSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        // TextView에 SpannableString 설정
        binding.introText.text = spannableString
    }


}