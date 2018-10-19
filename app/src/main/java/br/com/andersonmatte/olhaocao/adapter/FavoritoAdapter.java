package br.com.andersonmatte.olhaocao.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import br.com.andersonmatte.olhaocao.R;
import br.com.andersonmatte.olhaocao.entidade.Cao;
import es.dmoral.toasty.Toasty;

public class FavoritoAdapter extends ArrayAdapter<Cao> {

    private List<Cao> listaCaes;
    private Context context;
    private Boolean favorito;
    private AlertDialog alertDialog;

    public FavoritoAdapter(Context context, List<Cao> listaCaesRecebida, Boolean favorito) {
        super(context, 0, listaCaesRecebida);
        this.listaCaes = listaCaesRecebida;
        this.context = context;
        this.favorito = favorito;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.lista_favoritos, null);
        //Aqui ocorre a mágica no setTag onde é passado a posição da ListView!
        view.setTag(position);
        Button button = (Button) view.findViewById(R.id.botao_apagar);
        button.setTag(position);
        //Controle se imagem tem origem camera ou url do serviço.
        if (this.listaCaes != null && !this.listaCaes.isEmpty()){
            if (this.listaCaes.get(position) != null){
                if (this.listaCaes.get(position).getUrl() == null ||
                        (this.listaCaes.get(position).getUrl() != null
                                && this.listaCaes.get(position).getUrl().equals("N"))){
                    //Aqui ocorre a carga das imagens quando salvas no BD Realm;
                    if (this.listaCaes.get(position).getFoto() != null){
                        ImageView imageView = (ImageView) view.findViewById(R.id.imagemCao);
                        byte[] outImage = this.listaCaes.get(position).getFoto();
                        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
                        final Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
                        final String nomeImagem = "camera"+ position;
                        imageView.setImageBitmap(imageBitmap);
                        //Long press, abre modal com opção de salvar no dispositivo a imagem selecionada.
                        imageView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder builderAlert = new AlertDialog.Builder(context, R.style.AlertDialog);
                                builderAlert.setTitle(context.getResources().getString(R.string.download));
                                builderAlert.setMessage(context.getResources().getString(R.string.salvar_imagem));
                                builderAlert.setPositiveButton(context.getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        saveImage(imageBitmap, nomeImagem);
                                    }
                                });
                                builderAlert.setNegativeButton(context.getResources().getString(R.string.nao), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toasty.warning(context, context.getResources().getString(R.string.nao_salvar), Toast.LENGTH_SHORT, true).show();
                                    }
                                });
                                alertDialog = builderAlert.create();
                                alertDialog.show();
                                return true;
                            }
                        });
                    }
                } else {
                    //Aqui ocorre a carga das imagens com o Picasso,
                    //referência http://square.github.io/picasso/
                    Cao cao = getItem(position);
                    ImageView imageView = (ImageView) view.findViewById(R.id.imagemCao);
                    Picasso.get().load(cao.getUrl()).into(imageView);
                    //Long press, abre modal com opção de salvar no dispositivo a imagem selecionada.
                    imageView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builderAlert = new AlertDialog.Builder(context, R.style.AlertDialog);
                            builderAlert.setTitle(context.getResources().getString(R.string.download));
                            builderAlert.setMessage(context.getResources().getString(R.string.salvar_imagem));
                            builderAlert.setPositiveButton(context.getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new DownloadFromURL().execute(getItem(position).getUrl());
                                    Toasty.success(context, context.getResources().getString(R.string.salvar_imagem_download), Toast.LENGTH_SHORT, true).show();
                                }
                            });
                            builderAlert.setNegativeButton(context.getResources().getString(R.string.nao), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toasty.warning(context, context.getResources().getString(R.string.nao_salvar), Toast.LENGTH_SHORT, true).show();
                                }
                            });
                            alertDialog = builderAlert.create();
                            alertDialog.show();
                            return true;
                        }
                    });
                }
            }
        }
        return view;
    }

    //Realiza o download da imagem e salva no dispositivo se origem camera.
    public void saveImage(Bitmap bitmap, String name) {
        name = name + ".jpg";
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            FileOutputStream fileOutputStream = null;
            File targetFilePath = new File(path, name);
            FileOutputStream outputStream = new FileOutputStream(targetFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            Toasty.success(context, context.getResources().getString(R.string.salvar_imagem_download), Toast.LENGTH_SHORT, true).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Realiza o download da iumagem e salva no dispositivo se origem url.
    class DownloadFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... fileUrl) {
            int count;
            try {
                URL url = new URL(fileUrl[0]);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                FileOutputStream fileOutputStream = null;
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File targetFilePath = new File(path, Uri.parse(fileUrl[0]).getLastPathSegment());
                fileOutputStream = new FileOutputStream(targetFilePath);
                OutputStream outputStream = fileOutputStream;
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }
    }

}
