//operations

interface PaymentStrategy {
    pay(): void;
}

interface Credit extends PaymentStrategy {
    number: number;
}

interface Debit extends PaymentStrategy{
    newNumber: number;
}

//data

interface User {
    name: string;
}

interface DigitalAccount {
    accountNumber: number;
}

interface TraditionalAccount {
    accountNumber: number;
}

type UserFull = User | DigitalAccount | TraditionalAccount;
type UserFromDigital = User | DigitalAccount;
type UserTraditional = User | TraditionalAccount;

// generics

interface AppResponse<T> {
    statusCode: number;
    message: string;
    data: T;
}

const response: AppResponse<User> = {
    data: {
        name: "Tiago"
    },
    message: "something",
    statusCode: 1
};
