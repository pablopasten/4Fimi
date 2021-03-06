package cl.ryc.forfimi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cl.ryc.forfimi.comms.CommsSendPassword;
import cl.ryc.forfimi.comms.CommsSignIn;
import cl.ryc.forfimi.comms.GetIDCloudMessage;
import cl.ryc.forfimi.comms.LoginComms;
import cl.ryc.forfimi.entities.LoginUsuario;
import cl.ryc.forfimi.error.ErrorHandler;
import cl.ryc.forfimi.helpers.GlobalPersist;
import cl.ryc.forfimi.helpers.ValidateHelper;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;

public class Login extends AppCompatActivity {


    Button btnIngreso,btnRegistro;
    EditText Usuario, Password;
    static Context c;
    ValidateHelper helper;
    ProgressDialog pd;
    CallbackManager callbackManager;
    LoginButton loginButton;
    static String FacebookId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        GlobalPersist gp= GlobalPersist.getInstance(this);
        getControls();

        /***************************************************
         * Registramos el dispositivo en GCM sòlo si no existe previamente
         */

       if  (!gp.VaidateExistsKey("IDGCM")|| gp.getGlobalPersist("IDGCM").equals("")) {

           GetIDCloudMessage gidgcm = new GetIDCloudMessage(this);
           gidgcm.execute("");
       }


        /**************************************************
         * Validamos si el usaurio ya esta logueado anteriormente
         *
         ************************************************/


        if(gp.VaidateExistsKey("NombreUsuario"))
        {
            System.out.println("Entre!");
            Intent intent = new Intent(this, cl.ryc.forfimi.Menu.class);
            startActivity(intent);

        }
        else {



            /************************************************
             * BOTON FACEBOOOK
             */

            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);
            // LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
            loginButton.setReadPermissions(Arrays.asList(
                    "public_profile", "email", "user_birthday", "user_friends"));
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    System.out.println("Estoy logueado");
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v("LoginActivity", response.toString());

                                    // Application code
                                    try {
                                        FacebookId = object.getString("id");
                                        String email = object.getString("email");
                                        String Nombre = object.getString("name").substring(0, object.getString("name").indexOf(" "));
                                        String Apellido = object.getString("name").substring(object.getString("name").indexOf(" ") + 1, object.getString("name").length());
                                        Apellido = Apellido.substring(0, Apellido.indexOf(" "));

                                        System.out.println(email + " " + Nombre + " " + Apellido);

                                        pd = new ProgressDialog(c);
                                        pd.setMessage("Un Segundo estamos creando tu cuenta");

                                        //vamos a generar una paswword aleatorea para este usuario de largo 15
                                        Random randomGenerator = new Random();

                                        String alphabet = "123567890abcdefghijklmnopqrstuvwxyz";
                                        String pass = "";
                                        for (int i = 0; i < 15; i++) {
                                            pass = pass + (alphabet.charAt(randomGenerator.nextInt(alphabet.length())));
                                        } // prints 50 random characters from alphabet

                                        CommsSignIn cms = new CommsSignIn(pd, c, Nombre, "_", Apellido,
                                                "_", email, pass, "sin_info", 0);

                                        cms.execute("");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ; // 01/31/1980 format
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday");
                    request.setParameters(parameters);
                    request.executeAsync();

                }

                @Override
                public void onCancel() {
                    // App code
                    System.out.println("Cancelado");
                }

                @Override
                public void onError(FacebookException exception) {
                    System.out.println("Error:" + exception.getMessage().toString());
                }
            });

            /***************************************************
             * FIN FOTON FACEBOOK
             **************************************************/



        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void  getControls(){

        Usuario=(EditText) findViewById(R.id.editText);
        Password=(EditText) findViewById(R.id.edtPassword);
        btnIngreso=(Button) findViewById(R.id.button);
        btnRegistro=(Button) findViewById(R.id.btnRegistro);

        this.c=this;
        helper= ValidateHelper.getInstance();

        btnIngreso.setOnClickListener(onIngresar);
        btnRegistro.setOnClickListener(onRegistro);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Usuario.getText().toString().equals("")) {
                    SolicitudContraseña(Usuario.getText().toString());
                    Snackbar.make(view, "Enviaremos tu contraseña olvidada al correo registrado", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    Snackbar.make(view, "Ingresa tu usuario para restaurar tu contraseña", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

    }


    View.OnClickListener onIngresar = new View.OnClickListener()
    {

        public void onClick(View v)
        {
            if(helper.isHaveConnection(c)) {
                if (Usuario.getText().toString().equals("") || Password.getText().toString().equals("")) {

                    Snackbar.make(v, "Debes ingresar Usuario y Contraseña", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    /*Toast toast = Toast.makeText(c, "Debes ingresar Usuario y Contraseña", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                    toast.show();*/
                } else {
                    if (! helper.validateEmail(Usuario.getText().toString())) {
                        Snackbar.make(v, "Debes ingresar email valido", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        FacebookId="";
                        pd= new ProgressDialog(c);
                        pd.setMessage("Estamos validando tu cuenta");
                        LoginComms cl= new LoginComms(c,pd,Usuario.getText().toString(),Password.getText().toString(),0,0);
                        cl.execute("");
                    }

                }
            }
            else{
                Snackbar.make(v, "No tienes conexión a internet, intenta màs tarde", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }
    };

    View.OnClickListener onRegistro = new View.OnClickListener()
    {

        public void onClick(View v)
        {
            FacebookId="";
            Intent intent = new Intent(c.getApplicationContext(), cl.ryc.forfimi.Sigin.class);
            c.startActivity(intent);

        }
    };


    public static void onBack(LoginUsuario lu){
        ErrorHandler eh=ErrorHandler.getInstance();
        if(eh.getLastError()==0) {
            if (lu.getCod_salida() == 1) {
                Toast toast = Toast.makeText(c, "Error:" + lu.getDes_salida(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                toast.show();

            } else {

                GlobalPersist gp = GlobalPersist.getInstance(c);
                gp.setGlobalPersist("NombreUsuario", lu.getNombre());
                gp.setGlobalPersist("IdUsuario", "" + lu.getIdUsuario());
                gp.setGlobalPersist("TipoPerfil", "" + lu.getPerfil());
                if (!FacebookId.equals("")) {
                    gp.setGlobalPersist("FacebookID", FacebookId);
                }
                Intent intent = new Intent(c.getApplicationContext(), cl.ryc.forfimi.Menu.class);
                c.startActivity(intent);
            }
        }
        else{

            Toast toast = Toast.makeText(c,"Error de Conexion", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }

    public static void ErrorOnCreateUserWithFacebook()
    {
        Toast toast = Toast.makeText(c,"Se ha Producido un error al crear tu cuenta con facebook", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
        toast.show();
        LoginManager.getInstance().logOut();
    }

    public static void BackAffterGCM(){

    }


    public void SolicitudContraseña(String email)
    {
        CommsSendPassword csp= new CommsSendPassword(email);
        csp.execute("");
    }
}
