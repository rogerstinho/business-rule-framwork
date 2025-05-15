

## COCOMO Model Overview

The Basic COCOMO model estimates the effort and time required for software development projects based on the size of the project (measured in KLOC).

### **Formulas**
- **Effort (E)**: `E = a * (KLOC)^b` (in Person-Months)
- **Development Time (Tdev)**: `Tdev = c * (E)^d` (in Months)
- **Required Staff**: `Person Required = Effort / Time`

### **Constants**
| Software Project | a   | b    | c   | d    |
|:-----------------|:----|:-----|:----|:-----|
| ORGANIC          | 2.4 | 1.05 | 2.5 | 0.38 |
| SEMI_DETACHED    | 3.0 | 1.12 | 2.5 | 0.35 |
| EMBEDDED         | 3.6 | 1.20 | 2.5 | 0.32 |

---

## Example Usage: COCOMO Model

### **Scenario 1**: Simple COCOMO Model
- **Project Setup**:
    - Organic project.
    - 4,000 delivered source instructions (DSI).
- **Expected Results**:
    - Effort: ~10.289 person-months (±0.1%).
    - Development Time: ~6.062 months (±0.1%).
    - Required Staff: 2.
