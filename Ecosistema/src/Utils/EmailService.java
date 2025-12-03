package Utils;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {
    private static final String REMITENTE = "ecosistemas29@gmail.com";
    private static final String CONTRASENA_APLICACION = "svioftceqslhxxvr";

    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        try {
            Session session = crearSesion();

            Message correo = new MimeMessage(session);
            correo.setFrom(new InternetAddress(REMITENTE));
            correo.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            correo.setSubject(asunto);
            correo.setText(mensaje);

            Transport.send(correo);

        } catch (MessagingException e) {
            e.printStackTrace();  // MUESTRA EL ERROR REAL
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }
    }

    private Session crearSesion() {
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");  // TLS
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, CONTRASENA_APLICACION);
            }
        });
    }
}
