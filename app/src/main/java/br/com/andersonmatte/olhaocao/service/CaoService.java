package br.com.andersonmatte.olhaocao.service;

import br.com.andersonmatte.olhaocao.entidade.Cao;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface CaoService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("breeds/image/random")
    Call<Cao> getCaes();

}
