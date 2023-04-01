package com.example.swcertificatio

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.round
import kotlin.math.roundToInt


//TODO 학생들 점수차가 크기 떄문에 적절한 데이터 최대-최소 Nomalzation를 하여 그래프에 그려 넣기
// ToDO APP 전체적으로 ui 다시 고민하고 검토해보기

class RaderChartActivity : AppCompatActivity() {

    lateinit var radarChart: RadarChart
    private val list: ArrayList<RadarEntry> = ArrayList()
    private var Scorelist = mutableListOf<Float>()
    private val beforebutton: ImageButton by lazy { findViewById(R.id.beforeButton) }
    private val UsernameTextView: TextView by lazy { findViewById(R.id.UserNameTextView) }
    private val formatname = listOf("SW개발역량", "SW융합핵심역량", "SW전공역량", "SW창의역량", "오픈소스SW역량")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rader_chart)

        prevButtonClicked() // 이전 화면으로 돌아가는 함수
        Radarchartdraw() // RadarChart 그리는 함수
        retrofiservice() // retrofit을 이용해 API 데이터 가져오기

    }

    private fun Radarchartdraw() {
        radarChart = findViewById(R.id.Rader_Chart) // raderchart contect

        list.add(RadarEntry(40.0F))
        list.add(RadarEntry(30.0F))
        list.add(RadarEntry(50.0F))
        list.add(RadarEntry(35.0F))
        //   list.add(RadarEntry(0.0F))

        val radarDataset = RadarDataSet(list, "List")

        radarDataset.setColors(ColorTemplate.MATERIAL_COLORS, 255)
        radarDataset.lineWidth = 2f
        radarDataset.valueTextSize = 18f
        radarDataset.valueTextColor = Color.RED
        radarDataset.fillColor = Color.GREEN
        radarDataset.setDrawFilled(true)

        val radarData = RadarData()
        radarData.addDataSet(radarDataset)

        val xAxis: XAxis = radarChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(formatname)
        xAxis.textSize = 8F

        radarChart.getYAxis().setAxisMinimum(0.0F);
        radarChart.getYAxis().setAxisMaximum(100.0F);

        radarChart.data = radarData
        radarChart.description.text = "SW 핵심 역량 그래프";
        radarChart.animateY(2000)
    }

    private fun retrofiservice() {
        var SchoolNumber = intent.getStringExtra("SchoolNumber") // 학번 받아오기

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val UserGraphService = retrofit.create(SWservice::class.java)

        UserGraphService.getUser().enqueue(object : Callback<List<SWdataDto>> {
            override fun onResponse(
                call: Call<List<SWdataDto>>,
                response: Response<List<SWdataDto>>
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result = response.body()
                    result?.groupBy {
                        it.Swscore.groupBy {
                            if (it.schoolnumber == SchoolNumber!!.toInt()) {
                                list.add(RadarEntry(it.score))
                                UsernameTextView.text = "${it.name}님의 SW 역량 그래프입니다."
                            }
                        }
                        it.Swscore.forEach {
                            Scorelist.add(it.score)
                        }
                    }
                } else {
                    Log.d("jsm", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<List<SWdataDto>>, t: Throwable) {
                Log.d("jsm", "onFailure 에러: " + t.message.toString())
            }
        })
    }

    private fun prevButtonClicked() {
        beforebutton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Log.d(
                "test2",
                Scorelist.max().toString() + " " + Scorelist.min()
                    .toString() + " "+ScoreNomalzaition(45.0F)
            )
        }
    }

    private fun ScoreNomalzaition(score: Float) : Float {
        val NOM_Score = (score- Scorelist.min()) / (Scorelist.max() - Scorelist.min())
        val Result_Score = (NOM_Score * 100.0).roundToInt() / 100.0
        return Result_Score.toFloat()
    }

    companion object {
        private const val BASE_URL = "https://swcertification-oc483cgtj-smusung.vercel.app/"
    }

}



