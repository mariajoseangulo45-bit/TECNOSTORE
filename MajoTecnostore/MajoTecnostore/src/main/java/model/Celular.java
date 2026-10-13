package model;

public final class Celular {

    public static final int STOCK_BAJO = 5;

    private long id;
    private String marca;
    private String modelo;
    private String sistemaOperativo;
    private long idVersionSo;
    private CategoriaGama gama;
    private double precio;
    private double precioMayor;
    private Double precioEspecial;
    private int stock;

    public Celular(long id, String marca, String modelo, String sistemaOperativo, CategoriaGama gama,
            double precio, double precioMayor, Double precioEspecial, int stock) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.sistemaOperativo = sistemaOperativo;
        this.gama = gama;
        setPrecio(precio);
        setPrecioMayor(precioMayor);
        setPrecioEspecial(precioEspecial);
        setStock(stock);
    }

    public boolean tieneStockBajo() {
        return stock < STOCK_BAJO;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public long getIdVersionSo() {
        return idVersionSo;
    }

    public void setIdVersionSo(long idVersionSo) {
        this.idVersionSo = idVersionSo;
    }

    public CategoriaGama getGama() {
        return gama;
    }

    public void setGama(CategoriaGama gama) {
        this.gama = gama;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (precioMayor > precio) {
            throw new IllegalArgumentException("El precio unitario no puede ser menor al precio por mayor");
        }
        this.precio = precio;
    }

    public double getPrecioMayor() {
        return precioMayor;
    }

    public void setPrecioMayor(double precioMayor) {
        if (precioMayor <= 0) {
            throw new IllegalArgumentException("El precio por mayor debe ser mayor a 0");
        }
        if (precioMayor > precio) {
            throw new IllegalArgumentException("El precio por mayor no puede superar el precio unitario");
        }
        this.precioMayor = precioMayor;
    }

    public Double getPrecioEspecial() {
        return precioEspecial;
    }

    public void setPrecioEspecial(Double precioEspecial) {
        if (precioEspecial != null && precioEspecial <= 0) {
            throw new IllegalArgumentException("El precio especial debe ser mayor a 0");
        }
        this.precioEspecial = precioEspecial;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.stock = stock;
    }

    @Override
    public String toString() {
        return """
               *************************
               Id:                %s
               Marca:             %s
               Modelo:            %s
               Sistema operativo: %s
               Gama:              %s
               Precio unitario:   %s
               Precio por mayor:  %s
               Precio especial:   %s
               Stock:             %s
               """.formatted(id, marca, modelo, sistemaOperativo, gama, precio, precioMayor,
                precioEspecial == null ? "No tiene" : precioEspecial, stock);
    }
}
