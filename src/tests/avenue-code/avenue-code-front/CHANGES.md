# Alterações — Currency Converter

Arquivo modificado: `src/components/currency-converter/index.js`

---

## 1. Import do useState

```js
// ANTES
import React from "react";

// DEPOIS
import React, { useState } from "react";
```

---

## 2. Estados e lógica (adicionar após `function CurrencyConvertor() {`)

```js
// ADICIONAR
const [inputValue, setInputValue] = useState(defaultInput);
const [currency, setCurrency] = useState(defaultCurrency);
const selectedRate = exchangeRates.find((r) => r.currency === currency);
const convertedValue = (inputValue * selectedRate.rate).toFixed(3);
const handleReset = () => { setInputValue(defaultInput); };
```

---

## 3. Select — dinâmico e controlado

```jsx
// ANTES
<select className="mb-10" data-testid="select-currency">
  <option>CAD</option>
</select>

// DEPOIS
<select
  className="mb-10"
  data-testid="select-currency"
  value={currency}
  onChange={(e) => setCurrency(e.target.value)}
>
  {exchangeRates.map(({ currency: curr }) => (
    <option key={curr} value={curr}>{curr}</option>
  ))}
</select>
```

---

## 4. Input do valor (input-value) — controlado com placeholder dinâmico

```jsx
// ANTES
value="0"
placeholder="Enter value in USD"

// DEPOIS
value={inputValue}
onChange={(e) => setInputValue(e.target.value)}
placeholder={`Enter value in ${currency}`}
```

---

## 5. Input do resultado (converted-value) — valor calculado

```jsx
// ANTES
value="81.049"

// DEPOIS
value={convertedValue}
```

---

## 6. Botão — data-testid corrigido e onClick adicionado

```jsx
// ANTES
<button data-testid="clear-values">Clear Input</button>

// DEPOIS
<button data-testid="clear-value" onClick={handleReset}>Clear Input</button>
```
