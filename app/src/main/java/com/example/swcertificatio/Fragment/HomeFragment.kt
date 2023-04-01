package com.example.swcertificatio.Fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.swcertificatio.*
import com.example.swcertificatio.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private var schoolnumberlist = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentHomeBinding.bind(view)
        binding = fragmentBinding

        editTextchangebutton() // edittext에 의해 버튼 동작 유무 판단 함수
        retrofitservice() // retrofit을 이용해 API 데이터 가져오는 함수
        clickedButton() // Button 클릭 작동 함수
    }

    private fun retrofitservice() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val SWService = retrofit.create(SWservice::class.java)

        SWService.getUser().enqueue(object : Callback<List<SWdataDto>> {
            override fun onResponse(
                call: Call<List<SWdataDto>>,
                response: Response<List<SWdataDto>>
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result = response.body()
                    result?.groupBy {
                        it.Swscore.take(1).forEach {
                            binding.FirstName.text = it.name
                            binding.FirstScore.text = "${it.score} 점"
                        }
                        it.Swscore.take(2).forEach {
                            binding.SecondName.text = it.name
                            binding.SecondScore.text = "${it.score} 점"
                        }
                        it.Swscore.take(3).forEach {
                            binding.ThirdName.text = it.name
                            binding.ThirdScore.text = "${it.score} 점"
                        }
                        it.Swscore.forEach {
                            schoolnumberlist.add(it.schoolnumber.toString())
                            Log.d("test", it.name)
                            Log.d("test", it.schoolnumber.toString())
                            Log.d("test", it.score.toString())
                        }
                    }
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("jsm", "onResponse 실패")
                }
            }
            override fun onFailure(call: Call<List<SWdataDto>>, t: Throwable) {
                Log.d("jsm", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    private fun clickedButton() {
        binding.GraphButton.setOnClickListener {
            if (schoolnumberlist.any { it == binding.editText.text.toString() } == true) {
                val intent = Intent(context, RaderChartActivity::class.java)
                intent.putExtra("SchoolNumber", binding.editText.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(context, ButtonFailText, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editTextchangebutton() {
        binding.GraphButton.isEnabled = false

        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.editText.length() < 10) {
                    binding.GraphButton.setBackgroundColor(Color.GRAY)
                    binding.GraphButton.isClickable = false
                    binding.GraphButton.isEnabled = false
                } else {
                    binding.GraphButton.setBackgroundColor(Color.RED)
                    binding.GraphButton.isClickable = true
                    binding.GraphButton.isEnabled = true
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    companion object {
        private const val BASE_URL = "https://swcertification-oc483cgtj-smusung.vercel.app/"
        private const val ButtonFailText = "학번을 다시 한번 확인해주세요."
    }

}