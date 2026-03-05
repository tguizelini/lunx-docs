export class DateUtils {
  static convertToBrazilianDate(date: Date) {
    return date.toLocaleDateString('pt-BR');
  }
}
