package br.com.andersonmatte.olhaocao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.andersonmatte.olhaocao.R;
import br.com.andersonmatte.olhaocao.entidade.Cao;

public class CaoAdapter extends ArrayAdapter<Cao> {

    private List<Cao> listaCaes;
    private Context context;
    private Boolean favorito;

    public CaoAdapter(Context context, List<Cao> listaCaesRecebida, Boolean favorito) {
        super(context, 0, listaCaesRecebida);
        this.listaCaes = listaCaesRecebida;
        this.context = context;
        this.favorito = favorito;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.lista_caes, null);
        //Aqui ocorre a mágica no setTag onde é passado a posição da ListView!
        view.setTag(position);
        Button button = view.findViewById(R.id.botao_coracao);
        button.setTag(position);
        //Aqui ocorre a carga das imagens com o Picasso,
        //referência http://square.github.io/picasso/
        Cao cao = getItem(position);
        ImageView imageView = (ImageView) view.findViewById(R.id.imagemCao);
        Picasso.get().load(cao.getUrl()).into(imageView);
        return view;
    }

}
