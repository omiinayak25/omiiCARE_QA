/* One-off recon: log into OpenMRS O2 demo and dump the home tiles + registration form DOM. */
const { chromium } = require('@playwright/test');

(async () => {
  const browser = await chromium.launch({ channel: 'chrome', headless: true });
  const ctx = await browser.newContext();
  const page = await ctx.newPage();
  const log = (...a) => console.log(...a);
  try {
    await page.goto('https://o2.openmrs.org/openmrs/login.htm', { waitUntil: 'domcontentloaded', timeout: 45000 });
    log('LOGIN URL:', page.url());

    // pick a session location, then sign in
    await page.locator('li[id="Registration Desk"]').click({ timeout: 20000 });
    await page.locator('#username').fill('admin');
    await page.locator('#password').fill('Admin123');
    await page.locator('#loginButton').click();
    await page.waitForLoadState('networkidle', { timeout: 45000 });
    log('AFTER LOGIN URL:', page.url());

    // dump home app tiles
    const tiles = await page.$$eval('#apps a, .apps a, a.task, #tasks a', els =>
      els.map(e => ({ id: e.id, href: e.getAttribute('href'), text: (e.textContent || '').trim().replace(/\s+/g, ' ').slice(0, 40) }))
        .filter(t => t.text));
    log('HOME TILES:', JSON.stringify(tiles, null, 1));

    // logout link recon
    const logout = await page.$$eval('a[href*="logout"], #logout, .logout', els =>
      els.map(e => ({ id: e.id, href: e.getAttribute('href'), text: (e.textContent || '').trim() })));
    log('LOGOUT LINKS:', JSON.stringify(logout));

    // navigate to Register a Patient
    const reg = page.locator('a[id*="registerPatient"], a:has-text("Register a patient"), a:has-text("Register a Patient")').first();
    await reg.click({ timeout: 20000 });
    await page.waitForLoadState('networkidle', { timeout: 45000 });
    log('REGISTRATION URL:', page.url());

    const controls = await page.$$eval('input, select, textarea, button', els =>
      els.map(e => ({
        tag: e.tagName.toLowerCase(),
        type: e.getAttribute('type'),
        id: e.id,
        name: e.getAttribute('name'),
        placeholder: e.getAttribute('placeholder'),
        text: (e.tagName.toLowerCase() === 'button' ? (e.textContent || '').trim() : '').slice(0, 30),
        visible: !!(e.offsetParent !== null),
      })).filter(c => c.id || c.name || c.placeholder || c.text));
    log('REG FORM CONTROLS:', JSON.stringify(controls, null, 1));

    await page.screenshot({ path: '/tmp/omrs_register.png', fullPage: true });
    log('Saved /tmp/omrs_register.png');
  } catch (err) {
    log('RECON ERROR:', err.message);
    try { await page.screenshot({ path: '/tmp/omrs_recon_error.png', fullPage: true }); } catch {}
  } finally {
    await browser.close();
  }
})();
