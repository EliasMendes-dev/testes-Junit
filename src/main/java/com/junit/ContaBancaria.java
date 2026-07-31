package com.junit;

public class ContaBancaria {

    private double saldo;

    public ContaBancaria(double saldoInicial) {
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo");
        }
        this.saldo = saldoInicial;
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de depósito deve ser positivo");
        }
        saldo += valor;
    }

    public void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser positivo");
        }
        if (valor > saldo) {
            throw new IllegalStateException("Saldo insuficiente");
        }
        saldo -= valor;
    }

    public double getSaldo() {
        return saldo;
    }
}
