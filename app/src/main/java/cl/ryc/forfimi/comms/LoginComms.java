package cl.ryc.forfimi.comms;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.ryc.forfimi.Login;
import cl.ryc.forfimi.Sigin;
import cl.ryc.forfimi.entities.LoginUsuario;
import cl.ryc.forfimi.helpers.DataFromServer;
import cl.ryc.forfimi.helpers.Parametros;
import cl.ryc.forfimi.error.ErrorHandler;

/**
 * Created by RyC on 07/04/2016.
 */
public class LoginComms extends AsyncTask {

    ProgressDialog pd;
    Context c;
    String Usuario,Contraseña;
    JSONArray result;
    Parametros parametro;
    LoginUsuario lu;
    ErrorHandler eh;
    int Proviene;

    public LoginComms(Context con, ProgressDialog p,String User,String Password,int Prov){
        this.c=con;
        this.pd=p;
        this.Usuario=User;
        this.Contraseña=Password;
        eh=ErrorHandler.getInstance();
        Proviene=Prov;

    }

    @Override
    protected Object doInBackground(Object[] params) {
         DataFromServer dfs= new DataFromServer();
         parametro= Parametros.getInstance();
         String URL=parametro.getHOSTURL()+parametro.getPORT()+parametro.getAPI()+parametro.getServicioLogin()+"correo="+this.Usuario+
                 ";pass="+this.Contraseña;
         System.out.println(URL);
         result=dfs.GetDataFromServer(URL,1);


        if(result!=null && eh.getLastError()==0)
        {
            //Vamos a sacar los resultados del json

            for(int cont=0;cont<result.length();cont++)
            {
                JSONObject json_data=null;
                try
                {
                    json_data = this.result.getJSONObject(cont);
                    lu= new LoginUsuario();



                    this.lu.setDes_salida(json_data.getString("des_salida"));
                    this.lu.setIdUsuario(json_data.getInt("idUsuario"));
                    this.lu.setEstado(json_data.getInt("estado"));
                    this.lu.setNombre(json_data.getString("nombre"));
                    this.lu.setPerfil(json_data.getInt("perfil"));




                    //ViewsHelper.setStatusComms(NoError);

                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    // ViewsHelper.setStatusComms(JsonProblem);
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object result)
    {
        // TODO Auto-generated method stub
        super.onPostExecute(result);

        if(Proviene==0) {
            Login.onBack(this.lu);
        }
        else{
            Sigin.onBackCommsLogIn(this.lu);
        }
        pd.dismiss();
    }

    @Override
    protected void onPreExecute()
    {
        // TODO Auto-generated method stub
        super.onPreExecute();

        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();

    }
}
