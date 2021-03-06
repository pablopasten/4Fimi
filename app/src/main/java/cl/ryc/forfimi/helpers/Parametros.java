package cl.ryc.forfimi.helpers;

/**
 * Created by RyC on 07/04/2016.
 */
public class Parametros {


    /************************************************************
     * ESTA ES EL AREA DE CONFIGURACIONES DE SERVIDOR
     ************************************************************/
    private final String HOSTURL="http://200.111.141.82";
    private final String PATH1="";
    private final String PORT=":7010";
    private final String API="/fimi_v0/webapi/u/";
    private final String FTP="";
    private final String FTPUser="";
    private final String FTPPass="";
    private final String FTPPath="";

    /************************************************************
     * ESTA ES EL AREA DE CONFIGURACIONES DE WEBSERVICES
     ************************************************************/
    private final String ServicioLogin="login;";
    private final String ServicioHistorial="getHistorial";
    private final String ServicioCreaUSuario="setUser;";
    private final String ServicioUsuario="getUser;";
    private final String ServicioModificarUsuario="modUser;";
    private final String ServicioEnviaPalabras="setWord;";
    private final String ServicioSendPassword="getC;";
    private final String ServicioNoticias="getNoticias";
    private final String EditRSS="setRedes;";
    private final String chartService="getDatosGraf;";
    private final String getMensajesHistoricos="getHistorial100;";
    private final String LoginFacebook="lF;";





    private static Parametros instance;
    private Parametros()
    {

    }


    public static Parametros getInstance()
    {
        if(instance==null)
        {
            instance=new Parametros();
        }


        return instance;
    }

    /************************************************************
     * Metodos de obtención
     ************************************************************/





    public String getHOSTURL() {
        return HOSTURL;
    }
    public String getPATH1() {
        return PATH1;
    }
    public String getPORT() {
        return PORT;
    }
    public String getAPI() {
        return API;
    }
    public String getFTP() {
        return FTP;
    }
    public String getFTPUser() {
        return FTPUser;
    };
    public String getFTPPass() {
        return FTPPass;
    };
    public String getFTPPath() {
        return FTPPath;
    };

    public String getServicioLogin() {return ServicioLogin;};

    public String getServicioHistorial() {
        return ServicioHistorial;
    }

    public String getServicioCreaUSuario() {
        return ServicioCreaUSuario;
    }

    public String getServicioUsuario() {
        return ServicioUsuario;
    }

    public String getServicioModificarUsuario() {
        return ServicioModificarUsuario;
    }

    public String getServicioEnviaPalabras() {
        return ServicioEnviaPalabras;
    }

    public String getServicioSendPassword() {
        return ServicioSendPassword;
    }

    public String getServicioNoticias() {
        return ServicioNoticias;
    }

    public String getEditRSS() {
        return EditRSS;
    }

    public String getChartService() {
        return chartService;
    }

    public String getGetMensajesHistoricos() {
        return getMensajesHistoricos;
    }

    public String getLoginFacebook() {
        return LoginFacebook;
    }
}

