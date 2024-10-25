<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Salva Dati</title>
</head>
<body>
<%
    // Recupero dei dati inviati dal modulo
    String nome = request.getParameter("nome");
    String cognome = request.getParameter("cognome");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String comple = request.getParameter("comple");
    String radioOption = request.getParameter("radio_option");
    String domande = request.getParameter("domande");

    // Connessione al database
    Connection conn = null;
    PreparedStatement pstmt = null;
    try {
        // Imposta il driver JDBC e la connessione al database
        Class.forName("com.mysql.cj.jdbc.Driver"); // Assicurati di avere il driver corretto
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nome_database", "username", "password"); // Modifica con i tuoi dati

        // Query per inserire i dati nel database
        String sql = "INSERT INTO utenti (nome, cognome, email, password, comple, lavoro_studio, domande) VALUES (?, ?, ?, ?, ?, ?, ?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, nome);
        pstmt.setString(2, cognome);
        pstmt.setString(3, email);
        pstmt.setString(4, password);
        pstmt.setString(5, comple);
        pstmt.setString(6, radioOption);
        pstmt.setString(7, domande);

        // Esecuzione della query
        pstmt.executeUpdate();
        out.println("<h3>Dati salvati con successo!</h3>");

    } catch (Exception e) {
        e.printStackTrace();
        out.println("<h3>Errore nel salvataggio dei dati.</h3>");
    } finally {
        // Chiusura delle risorse
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
%>
</body>
</html>