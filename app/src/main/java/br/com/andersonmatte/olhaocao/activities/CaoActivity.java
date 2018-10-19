package br.com.andersonmatte.olhaocao.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.andersonmatte.olhaocao.R;
import br.com.andersonmatte.olhaocao.base.ActivityMenuBase;
import br.com.andersonmatte.olhaocao.entidade.Cao;
import br.com.andersonmatte.olhaocao.service.CaoService;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaoActivity extends ActivityMenuBase {

    private List<Cao> listaCaes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_cao);
        //Cria o Tonteador.
        progress = new ProgressDialog(this);
        //Busca na API https://api.thedogapi.com/v1/ as imagens dos cães.
        this.prebuscaCaes();
        //Clique do botão buscar mais imagens de Cães.
        Button buttonBuscarUsuario = (Button) findViewById(R.id.buttonCao);
        buttonBuscarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prebuscaCaes();
            }
        });
    }

    //Simula o fluxo de lista, chamada do service é random.
    public void prebuscaCaes(){
        this.listaCaes = new ArrayList<Cao>();
        for (int i = 0; i < 15; i++){
            buscaCaes();
        }
    }

    //Busca as imagens e de cães e retosna em uma lista de url de imagens
    // com o auxilio do Adapter e do Picasso as imagens são exibidas na tela.
    public void buscaCaes() {
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage(this.getResources().getString(R.string.warning));
        progress.setCancelable(true);
        progress.show();
        CaoService retrofitTheDogApi = CaoService.retrofit.create(CaoService.class);
        retrofitTheDogApi.getCaes().enqueue(new Callback<Cao>() {
            @Override
            public void onResponse(Call<Cao> call, Response<Cao> response) {
                if (response != null && response.body() != null) {
                    Cao cao = new Cao();
                    cao = response.body();
                    listaCaes.add(cao);
                }
                if (listaCaes != null){
                    CaoActivity.super.populaListViewCaes(listaCaes, false);
                    progress.hide();
                }
            }
            @Override
            public void onFailure(Call<Cao> call, Throwable t) {
                Log.i("ERRO", getResources().getString(R.string.erro_buscar_cao) + t.getMessage());
            }
        });
    }

    //Pega a linha selecionada na ListView.
    public void onClickFavorito(View view){
        if (view.getTag() != null){
            this.salvar((Integer) view.getTag());
        }
    }

    //Salva o objeto no Banco,
    public void salvar(int posicao) {
        super.realm.beginTransaction();
        Cao caoSalvar = this.listaCaes.get(posicao);
        super.realm.insertOrUpdate(caoSalvar);
        super.realm.commitTransaction();
        Toasty.success(this, getResources().getString(R.string.registro_favoritado), Toast.LENGTH_SHORT, true).show();
    }

    public List<Cao> getListaCaes() {
        return listaCaes;
    }

    public void setListaCaes(List<Cao> listaCaes) {
        this.listaCaes = listaCaes;
    }

}
