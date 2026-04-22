package com.freakyworld.service;

import com.freakyworld.domain.DetalleFactura;
import com.freakyworld.domain.Factura;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PdfFacturaService {

    public byte[] generarPdf(Factura factura, List<DetalleFactura> detalles) {
        try {
            StringBuilder html = new StringBuilder();

            html.append("""
    <html xmlns='http://www.w3.org/1999/xhtml'>
    <head>
        <meta charset='UTF-8' />
        <style>
            body {
                font-family: Arial, sans-serif;
                font-size: 12px;
                color: #222;
                margin: 20px;
            }
            .encabezado {
                text-align: center;
                margin-bottom: 25px;
                border-bottom: 2px solid #6f42c1;
                padding-bottom: 10px;
            }
            .encabezado h1 {
                margin: 0;
                color: #6f42c1;
                font-size: 26px;
            }
            .encabezado p {
                margin: 4px 0 0 0;
                color: #555;
            }
            .bloque {
                margin-bottom: 20px;
                border: 1px solid #ddd;
                border-radius: 8px;
                padding: 12px;
            }
            .fila {
                margin-bottom: 8px;
            }
            .etiqueta {
                font-weight: bold;
                display: inline-block;
                width: 160px;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 10px;
            }
            th, td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
                vertical-align: middle;
            }
            th {
                background-color: #f3f0ff;
                color: #333;
            }
            .total {
                text-align: right;
                font-size: 18px;
                font-weight: bold;
                margin-top: 15px;
                color: #6f42c1;
            }
        </style>
    </head>
    <body>
""");

            html.append("<div class='encabezado'>");
            html.append("<h1>FreakyWorld</h1>");
            html.append("<p>Factura de compra</p>");
            html.append("</div>");

            html.append("<div class='bloque'>");
            html.append("<div class='fila'><span class='etiqueta'>Factura:</span> " + factura.getIdFactura() + "</div>");
            html.append("<div class='fila'><span class='etiqueta'>Fecha:</span> " + escaparHtml(String.valueOf(factura.getFechaPedido())) + "</div>");
            html.append("<div class='fila'><span class='etiqueta'>Estado:</span> " + escaparHtml(factura.getEstado()) + "</div>");
            html.append("<div class='fila'><span class='etiqueta'>Método de pago:</span> " + escaparHtml(factura.getMetodoPago()) + "</div>");
            html.append("<div class='fila'><span class='etiqueta'>Seguimiento:</span> " + escaparHtml(factura.getNumeroSeguimiento()) + "</div>");
            html.append("<div class='fila'><span class='etiqueta'>Cliente:</span> " + escaparHtml(factura.getUsuario().getNombre()) + "</div>");
            html.append("<div class='fila'><span class='etiqueta'>Correo:</span> " + escaparHtml(factura.getUsuario().getCorreo()) + "</div>");
            html.append("<div class='fila'><span class='etiqueta'>Dirección:</span> "
                    + escaparHtml(factura.getUsuario().getDireccion() != null
                            ? factura.getUsuario().getDireccion()
                            : "Sin dirección registrada")
                    + "</div>");
            html.append("</div>");

            html.append("<table>");
            html.append("<thead>");
            html.append("<tr>");
            html.append("<th>Producto</th>");
            html.append("<th>Cantidad</th>");
            html.append("<th>Precio unitario</th>");
            html.append("<th>Subtotal</th>");
            html.append("</tr>");
            html.append("</thead>");
            html.append("<tbody>");

            for (DetalleFactura detalle : detalles) {
                html.append("<tr>");
                html.append("<td>").append(escaparHtml(detalle.getProducto().getNombre())).append("</td>");
                html.append("<td>").append(detalle.getCantidad()).append("</td>");
                html.append("<td>CRC ").append(detalle.getPrecioUnitario()).append("</td>");
                html.append("<td>CRC ").append(detalle.getSubtotal()).append("</td>");
                html.append("</tr>");
            }

            html.append("</tbody>");
            html.append("</table>");

            html.append("<div class='total'>Total pagado: CRC ").append(factura.getTotal()).append("</div>");
            html.append("</body></html>");

            ByteArrayOutputStream salida = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html.toString(), null);
            builder.toStream(salida);
            builder.run();

            return salida.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo generar el PDF de la factura: " + e.getMessage());
        }
    }

    private String escaparHtml(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}