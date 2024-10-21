package com.example.calculadora_con_gui_1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Variables globales
    private double valorActual = 0;
    private double valorPrevio = 0;
    private String operador = "";
    private boolean operadorPulsado = false;
    private String resultadoAnterior = "";
    private String operacionEnProceso = "";
    private TextView resultado;
    private TextView mostrarHistorial;
    private List<String> historialOperaciones; // Lista para guardar el historial de operaciones

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resultado = findViewById(R.id.mostrar_resultado);
        mostrarHistorial = findViewById(R.id.mostrar_historial);

        // Inicializar la lista de historial
        historialOperaciones = new ArrayList<>();

        // Iniciar las vistas de los operadores
        Button suma = findViewById(R.id.operador_sumar);
        Button resta = findViewById(R.id.operador_restar);
        Button multiplicar = findViewById(R.id.operador_multiplicar);
        Button dividir = findViewById(R.id.operador_dividir);
        Button porcentaje = findViewById(R.id.operador_porcentaje);
        Button igual = findViewById(R.id.boton_igual);
        Button coma = findViewById(R.id.boton_coma);
        Button c = findViewById(R.id.boton_c);
        Button ac = findViewById(R.id.boton_ac);
        Button delete = findViewById(R.id.boton_delete);
        Button botonHistorial = findViewById(R.id.boton_historial); // Botón de historial

        // Botones numéricos
        Button cero = findViewById(R.id.numero_0);
        Button uno = findViewById(R.id.numero_1);
        Button dos = findViewById(R.id.numero_2);
        Button tres = findViewById(R.id.numero_3);
        Button cuatro = findViewById(R.id.numero_4);
        Button cinco = findViewById(R.id.numero_5);
        Button seis = findViewById(R.id.numero_6);
        Button siete = findViewById(R.id.numero_7);
        Button ocho = findViewById(R.id.numero_8);
        Button nueve = findViewById(R.id.numero_9);

        // Convertir los botones numéricos en listeners
        cero.setOnClickListener(v -> agregarNumero("0", resultado));
        uno.setOnClickListener(v -> agregarNumero("1", resultado));
        dos.setOnClickListener(v -> agregarNumero("2", resultado));
        tres.setOnClickListener(v -> agregarNumero("3", resultado));
        cuatro.setOnClickListener(v -> agregarNumero("4", resultado));
        cinco.setOnClickListener(v -> agregarNumero("5", resultado));
        seis.setOnClickListener(v -> agregarNumero("6", resultado));
        siete.setOnClickListener(v -> agregarNumero("7", resultado));
        ocho.setOnClickListener(v -> agregarNumero("8", resultado));
        nueve.setOnClickListener(v -> agregarNumero("9", resultado));

        // Manejar las operaciones
        suma.setOnClickListener(v -> operar(resultado, "+"));
        resta.setOnClickListener(v -> operar(resultado, "-"));
        multiplicar.setOnClickListener(v -> operar(resultado, "*"));
        dividir.setOnClickListener(v -> operar(resultado, "/"));

        // Lógica botón porcentaje
        porcentaje.setOnClickListener(v -> {
            valorActual = valorActual / 100;
            resultado.setText(String.valueOf(valorActual));
        });

        // Lógica botón igual
        igual.setOnClickListener(v -> {
            if (!operador.isEmpty()) { // Asegurarse de que hay un operador seleccionado
                double resultadoCalculado = calcularResultado();
                if (resultadoCalculado == Double.MIN_VALUE) {
                    resultado.setText("No se puede dividir por 0");
                } else {
                    resultado.setText(String.valueOf(resultadoCalculado));
                    valorActual = resultadoCalculado; // Actualiza el valor actual para futuras operaciones
                    guardarResultado(String.valueOf(resultadoCalculado)); // Guardar el resultado para el historial
                    actualizarHistorial(operacionEnProceso, resultadoCalculado); // Actualizar el historial
                    operador = ""; // Limpiar el operador para próximas operaciones
                }
            }
        });

        // Lógica para el botón delete
        delete.setOnClickListener(v -> {
            String currenText = resultado.getText().toString();
            if (currenText.length() > 0) {
                resultado.setText(currenText.substring(0, currenText.length() - 1));
                if (currenText.length() > 1) {
                    valorActual = Double.parseDouble(resultado.getText().toString());
                } else {
                    valorActual = 0; // Establecer valorActual a 0 si no hay texto
                }
            }
        });

        // Lógica botón AC
        ac.setOnClickListener(v -> {
            resultado.setText(""); // Limpiar el resultado
            valorActual = 0;
            valorPrevio = 0;
            operador = "";
            mostrarHistorial.setText(""); // Limpiar el historial al reiniciar
            historialOperaciones.clear(); // Limpiar la lista de historial
        });

        // Lógica para el botón C (Clear y limpiar historial)
        c.setOnClickListener(v -> {
            resultado.setText(""); // Limpiar solo la entrada actual
            valorActual = 0; // Resetea valorActual
            resultadoAnterior = ""; // Limpiar el historial
        });

        // Lógica para el botón coma
        coma.setOnClickListener(v -> {
            agregarDecimal(resultado);
        });

        // Lógica para el botón de historial
        botonHistorial.setOnClickListener(v -> mostrarHistorialOperaciones());
    }

    private void agregarNumero(String numero, TextView resultado) {
        if (operadorPulsado) {
            resultado.setText(""); // Limpiar el resultado si se ha pulsado un operador
            operadorPulsado = false;
        }
        resultado.append(numero);
        valorActual = Double.parseDouble(resultado.getText().toString());
    }

    private void agregarDecimal(TextView resultado) {
        String currentText = resultado.getText().toString();
        // Permitir solo un decimal (ya sea '.' o ',')
        if (!currentText.contains(",") && !currentText.contains(".")) {
            resultado.append("."); // Puedes usar "," o "." dependiendo de tu preferencia
            valorActual = Double.parseDouble(resultado.getText().toString().replace(",", ".")); // Reemplazar ',' por '.' para la conversión
        }
    }

    private void operar(TextView resultado, String operadorSeleccionado) {
        if (!operadorPulsado) {
            valorPrevio = valorActual; // Guardar el valor actual como el previo
            operador = operadorSeleccionado; // Asignar el operador
            operadorPulsado = true; // Marcar que un operador ha sido pulsado
            operacionEnProceso = valorPrevio + operador; // Actualizar la operación en proceso
        } else {
            // Si ya se ha pulsado un operador, actualiza solo el operador
            operador = operadorSeleccionado;
        }
    }

    private void actualizarHistorial(String operacion, double resultado) {
        String historialTexto = operacion + " = " + resultado;
        mostrarHistorial.setText(historialTexto); // Actualiza el historial
        historialOperaciones.add(historialTexto); // Agregar la operación al historial
    }

    public void guardarResultado(String resultado) {
        resultadoAnterior = resultado;
        operacionEnProceso += resultado; // Concatenar el resultado a la operación en proceso
    }

    private double calcularResultado() {
        double resultado = 0;
        switch (operador) {
            case "+":
                resultado = valorPrevio + valorActual;
                break;
            case "-":
                resultado = valorPrevio - valorActual;
                break;
            case "*":
                resultado = valorPrevio * valorActual;
                break;
            case "/":
                if (valorActual != 0) {
                    resultado = valorPrevio / valorActual;
                } else {
                    return Double.MIN_VALUE; // Valor para indicar un error
                }
                break;
        }
        return resultado;
    }

    private void mostrarHistorialOperaciones() {
        if (!historialOperaciones.isEmpty()) {
            String ultimaOperacion = historialOperaciones.get(historialOperaciones.size() - 1);
            String[] partes = ultimaOperacion.split(" = ");
            if (partes.length > 1) {
                String resultadoHistorial = partes[1]; // Obtener el resultado de
                // Obtener el resultado del historial
                resultadoHistorial = partes[1];
                // Actualizar el TextView de resultado con el valor del historial
                resultado.setText(resultadoHistorial);
                valorActual = Double.parseDouble(resultadoHistorial); // Actualizar el valor actual con el resultado del historial
            }
        }
    }
}
