# ⚡ Speed Optimization Guide for Playwright Tests

This guide outlines advanced techniques and options to reduce execution, setup, and compilation times for Playwright tests in the Admin React App.

---

## 🏎️ Quick Optimization Summary

| Optimization Method | Configuration / Command | Expected Speedup | Recommended Use Case |
| :--- | :--- | :--- | :--- |
| **Target Single File** | `npx playwright test <filename>` | 🟢 **90% - 95% faster** | Active development on a specific feature |
| **Grep Specific Test** | `npx playwright test -g "<pattern>"` | 🟢 **95%+ faster** | Debugging/testing a single test case |
| **Parallel Workers** | `workers: undefined` (Uses CPU Cores) | 🟡 **2x - 3x faster** | Running the full test suite locally |
| **Disable Recording** | Disable trace/video capture in config | 🟡 **15% - 25% faster** | Speeding up routine local tests |
| **Sharding** | `npx playwright test --shard=1/3` | 🟡 **3x faster** (multi-CI) | Running full suites in CI pipelines |

---

## 🛠️ 1. Target Specific Tests (Avoid Running Everything)
When editing or fixing a feature, running all 129 tests is unnecessary. You can target specific files or individual test names:

### A. Run a Single Test File
Instead of `npm run test` (which runs all files), specify the file name:
```bash
npx playwright test tests/tests/login.spec.js
```

### B. Run a Single Test Case (By Name)
Use the `-g` (grep) flag followed by a string pattern of the test description:
```bash
npx playwright test -g "should login successfully with valid credentials"
```

---

## ⚙️ 2. Optimize Playwright Config Settings
You can modify [playwright.config.js](file:///Users/hiteshyadav/Desktop/PARKING-APP/admin_react_app/playwright.config.js) to reduce local execution overhead:

### A. Disable Video and Trace Capturing
Recording videos and generating trace zip archives for every test consumes substantial CPU and disk writing time. Turn them off locally:
```javascript
  use: {
    trace: 'off',                // Options: 'off', 'on-first-retry', 'retain-on-failure'
    video: 'off',                // Options: 'off', 'retain-on-failure'
    screenshot: 'off',           // Options: 'off', 'only-on-failure'
  }
```

### B. Parallelize Tests Inside a File (Only for Independent UI Tests)
Currently, tests within a single spec file run sequentially to avoid state collisions. If you have a file that doesn't modify data (e.g. `dashboard.spec.js` or `settings.spec.js`), you can enable parallel execution inside that file by adding this line at the top of the file:
```javascript
test.describe.configure({ mode: 'parallel' });
```

---

## 🌐 3. Use Network Mocking (Bypassing the Backend Database)
Tests that make real database requests are throttled by network speed and transaction delays. Playwright can mock API responses to run tests instantly.

In your test files, use `page.route` to mock slow API endpoints:
```javascript
// Intercepts dashboard API and returns mock data immediately (no database query)
await page.route('**/api/dashboard**', async route => {
  await route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify({
      status: "success",
      data: { active_sessions: 5, revenue: 12000 }
    })
  });
});
```

---

## ☁️ 4. Test Sharding (For CI/CD Pipelines)
If you want to run the full suite in a Github Action or other CI environment, you can shard the test execution across multiple parallel machines:
* **Runner 1:** `npx playwright test --shard=1/3`
* **Runner 2:** `npx playwright test --shard=2/3`
* **Runner 3:** `npx playwright test --shard=3/3`
This distributes the 129 tests evenly, completing the run 3x faster.
