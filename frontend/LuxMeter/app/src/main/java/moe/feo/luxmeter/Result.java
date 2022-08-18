package moe.feo.luxmeter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class Result extends AppCompatActivity {

    private static Result instance;
    private TextView classificacao, iluminacaoTitle,pavimentoTitle, pavimentoValue, areaTitle, areaValue;
    private ImageView imagemEficiencia;
    private static final DecimalFormat df = new DecimalFormat("0.00");


    private LottieAnimationView loadingAnimation, circleAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_result);

        getSupportActionBar().setTitle("Eficiência da Sala");

        iluminacaoTitle = findViewById(R.id.iluminacaoTitle);

        pavimentoTitle = findViewById(R.id.pavimentoTitle);

        pavimentoValue = findViewById(R.id.pavimentoValue);

        areaTitle = findViewById(R.id.areaTitle);

        areaValue = findViewById(R.id.areaValue);

        imagemEficiencia = findViewById(R.id.imagemEficiencia);

        loadingAnimation = findViewById(R.id.loadingAnimation);

        classificacao = findViewById(R.id.classificacao);

        circleAnimation = findViewById(R.id.circle);

        String jsonQRCode = getIntent().getStringExtra("jsonQRCode");

        //classificacao.setText(getIntent().getStringExtra("iluminanceValue"));




        try {
            JSONObject jsonOBJ = new JSONObject(jsonQRCode);

            pavimentoValue.setText(jsonOBJ.getString("sala"));//seta o texto do nome do pavimento com o nome da sala obtido

            Double area = jsonOBJ.getDouble("largura")*jsonOBJ.getDouble("comprimento");

            areaValue.setText(df.format(area)+"m²");//seta o texto da area com o nome da sala obtido


            Double iluminanceValue = Double.valueOf(getIntent().getStringExtra("iluminanceValue").replace("lx","")); //se não tiver sensor de luz dará erro aqui

            jsonOBJ.put("iluminanciaMediaFinal",iluminanceValue);//add valor do sensor no json

            jsonOBJ.put("aparelho",getDeviceName());//add marca e modelo do aparelho ao json

            RequestQueue queue = Volley.newRequestQueue(this);

            String url = Api.baseURL+"eficienciaResultado";



            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, jsonOBJ, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //response /resultado da API

                            try {

                                String resultadoClassificao = response.getString("classificacao");

                                classificacao.setText(resultadoClassificao);
                                iluminacaoTitle.setVisibility(View.VISIBLE);
                                pavimentoTitle.setVisibility(View.VISIBLE);
                                pavimentoValue.setVisibility(View.VISIBLE);
                                areaTitle.setVisibility(View.VISIBLE);
                                areaValue.setVisibility(View.VISIBLE);
                                imagemEficiencia.setVisibility(View.VISIBLE);
                                circleAnimation.setVisibility(View.VISIBLE);

                                classificacao.setVisibility(View.VISIBLE);
                                loadingAnimation.setVisibility(View.INVISIBLE);


                            } catch (JSONException e) {
                                e.printStackTrace();


                            }


                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {//caso dê erro na conexão
                            // TODO: Handle error
                            Toast.makeText(Result.instance,"Falha de conexão!",Toast.LENGTH_LONG).show();
                            instance.finish();

                        }
                    });

            queue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Result.instance,"Este aparelho não possui sensor de luz!",Toast.LENGTH_LONG).show();
            instance.finish();
        }




    }

    private String getDeviceName() { //retorna o nome do celular que está sendo usado
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        String model = Build.MODEL.toLowerCase();
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + "-" + model;
        }
    }
}