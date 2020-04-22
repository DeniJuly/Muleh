package com.rpl.muleh.method;

public class Transportasi {
    private String idUser, id, nama, nomor, foto, status, asal, tujuan;

    public Transportasi() {
    }

    public Transportasi(String idUser, String id, String nama, String nomor, String foto, String status, String asal, String tujuan) {
        this.idUser = idUser;
        this.id = id;
        this.nama = nama;
        this.nomor = nomor;
        this.foto = foto;
        this.status = status;
        this.asal = asal;
        this.tujuan = tujuan;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getId() { return id; }

    public String getNama() {
        return nama;
    }

    public String getNomor() {
        return nomor;
    }

    public String getFoto() {
        return foto;
    }

    public String getStatus() {
        return status;
    }

    public String getAsal() {
        return asal;
    }

    public String getTujuan() {
        return tujuan;
    }
}
