package br.com.andersonmatte.olhaocao.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.andersonmatte.olhaocao.R;
import br.com.andersonmatte.olhaocao.activities.CaoActivity;
import br.com.andersonmatte.olhaocao.activities.FavoritosActivity;
import br.com.andersonmatte.olhaocao.activities.HomeActivity;
import br.com.andersonmatte.olhaocao.adapter.CaoAdapter;
import br.com.andersonmatte.olhaocao.adapter.FavoritoAdapter;
import br.com.andersonmatte.olhaocao.entidade.Cao;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;

public class ActivityMenuBase extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected ProgressDialog progress;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView navigationView;
    protected Realm realm;
    private AlertDialog alertDialog;

    protected void onCreateDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.abrir, R.string.fechar);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this.onCreateDrawer();
        this.criaBancoRealm();
    }

    public void criaBancoRealm() {
        Realm.init(this);
        realm = Realm.getDefaultInstance();
    }

    //Botao voltar com suporte bread crumb.
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle != null && mActionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                Intent a = new Intent(ActivityMenuBase.this, HomeActivity.class);
                startActivity(a);
                break;
            case R.id.nav_caes:
                Intent b = new Intent(ActivityMenuBase.this, CaoActivity.class);
                startActivity(b);
                break;
            case R.id.nav_favoritos:
                Intent c = new Intent(ActivityMenuBase.this, FavoritosActivity.class);
                startActivity(c);
                break;
            case R.id.nav_sair:
                this.saidaAPP();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Aqui ocorre a saída do APP, com a confirmação do usuário.
    public void saidaAPP(){
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(this, R.style.AlertDialog);
        builderAlert.setTitle(getResources().getString(R.string.sair));
        builderAlert.setMessage(getResources().getString(R.string.sair_app));
        builderAlert.setPositiveButton(getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Fecha o APP.
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                finish();
        }
        });
        builderAlert.setNegativeButton(getResources().getString(R.string.nao), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toasty.info(ActivityMenuBase.this, getResources().getString(R.string.nao_complemento), Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog = builderAlert.create();
        alertDialog.show();
    }

    //Popula a listView com as imagens dos cães.
    public void populaListViewCaes(List<Cao> listaCaesRecebida, Boolean favorito){
        ListView listView = (ListView) findViewById(R.id.listView);
        if (favorito){
            final FavoritoAdapter adapter = new FavoritoAdapter(this, listaCaesRecebida, favorito);
            listView.setAdapter(adapter);
        } else {
            final CaoAdapter adapter = new CaoAdapter(this, listaCaesRecebida, favorito);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
