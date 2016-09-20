package com.desarrolladorandroid.cobrocontarjeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Token;


public class MainActivity extends AppCompatActivity implements Token.CreateToken {
    EditText numeroTarjeta, nombre, mes, anio, cvv;
    List<EditText> campos;
    int MY_SCAN_REQUEST_CODE = 0;
    private String stringTarjeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            stringTarjeta = savedInstanceState.getString("tarjeta");
        }
        setContentView(R.layout.activity_main);
        numeroTarjeta = (EditText) findViewById(R.id.noTarjeta);
        nombre = (EditText) findViewById(R.id.nombre);
        mes = (EditText) findViewById(R.id.mes);
        anio = (EditText) findViewById(R.id.anio);
        cvv = (EditText) findViewById(R.id.cvv);
        campos = Arrays.asList(numeroTarjeta, nombre, mes, anio, cvv);
        Conekta.setPublicKey(BuildConfig.publicKeyConekta);
        Conekta.collectDevice(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu men) {
        getMenuInflater().inflate(R.menu.menuprincipal, men);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.guardarmenu) {
            if (verificaContenido()) {

                stringTarjeta = stringTarjeta.replace(" ", "").trim();///se agrego esta linea para quitar los espacios por que si no no funciona
                Card card = new Card(nombre.getText().toString(), stringTarjeta, cvv.getText().toString(),
                        mes.getText().toString(), anio.getText().toString());

                Token token = new Token(this);

                token.onCreateTokenListener(this);
                token.create(card);

            } else {
                Toast.makeText(this, getString(R.string.faltancampos), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verificaContenido() {
        boolean falta = false;
        for (EditText texto : campos) {
            texto.setHintTextColor(ContextCompat.getColor(this, R.color.accent));
        }
        for (EditText texto : campos) {
            if (texto.getText().toString().trim().isEmpty()) {
                texto.setHintTextColor(ContextCompat.getColor(this, R.color.error));
                falta = true;
            }
        }
        return !falta;
    }

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, ContextCompat.getColor(this, R.color.primary_dark));
        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                stringTarjeta = scanResult.getFormattedCardNumber() + "\n";
                numeroTarjeta.setText(scanResult.getRedactedCardNumber());
                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    mes.setText(String.valueOf(scanResult.expiryMonth));//se agrego el string value of
                    anio.setText(String.valueOf(scanResult.expiryYear));//se agrego el string value of
                }

                if (scanResult.cvv != null) {
                    cvv.setText(scanResult.cvv);
                }

            } else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }

    @Override
    public void onCreateTokenReady(JSONObject data) {
        try {
            //TODO: Create charge
            Log.d("Token::::", data.getString("id"));
            Toast.makeText(this, "Token Creado", Toast.LENGTH_SHORT).show();
        } catch (Exception err) {
            //TODO: Handle error
            Log.d("Error: ", err.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tarjeta", stringTarjeta);
        super.onSaveInstanceState(outState);
    }
}
