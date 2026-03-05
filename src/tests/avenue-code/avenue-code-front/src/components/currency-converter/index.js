import React, { useState } from "react";
import {
  exchangeRates,
  defaultCurrency,
  defaultInput,
} from "../../data/exchangeRates";
import { preventNegative } from "../../utils/preventNegative";
import "./styles.css";

function CurrencyConvertor() {
  const [inputValue, setInputValue] = useState(defaultInput);
  const [currency, setCurrency] = useState(defaultCurrency);

  const selectedRate = exchangeRates.find((r) => r.currency === currency);
  const convertedValue = (inputValue * selectedRate.rate).toFixed(3);

  const handleReset = () => {
    setInputValue(defaultInput);
  };

  return (
    <div>
      <div className="layout-row justify-content-space-evenly min-height mt-75">
        <div className="layout-column w-35 pa-30 card">
          <select
            className="mb-10"
            data-testid="select-currency"
            value={currency}
            onChange={(e) => setCurrency(e.target.value)}
          >
            {exchangeRates.map(({ currency: curr }) => (
              <option key={curr} value={curr}>
                {curr}
              </option>
            ))}
          </select>
          <input
            className="h-50"
            type="number"
            value={inputValue}
            onKeyDown={preventNegative}
            onChange={(e) => setInputValue(e.target.value)}
            placeholder={`Enter value in ${currency}`}
            data-testid="input-value"
          />
        </div>

        <div className="layout-column w-35 pa-30 card">
          <h3 className="mb-10 mt-0">USD</h3>
          <input
            className="h-50"
            type="number"
            value={convertedValue}
            readOnly
            data-testid="converted-value"
          />
        </div>
      </div>

      <div className="layout-row justify-content-center pa-20">
        <button data-testid="clear-value" onClick={handleReset}>
          Clear Input
        </button>
      </div>
    </div>
  );
}

export default CurrencyConvertor;
