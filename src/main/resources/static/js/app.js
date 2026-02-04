(() => {
  // ===== Single-stimulus Stroop (existing) =====
  const sessionId = window.__SESSION_ID__;
  const elStimulus = document.getElementById('stimulus');
  const elChoices  = document.getElementById('choices');
  const elStatus   = document.getElementById('status');
  const elStart    = document.getElementById('startBtn');
  const elTimer    = document.getElementById('timer');
  const elTrials   = document.getElementById('sumTrials');
  const elCorrect  = document.getElementById('sumCorrect');
  const elAcc      = document.getElementById('sumAccuracy');
  const elAvg      = document.getElementById('sumAvg');
  const cbMode     = document.getElementById('cbMode');

  let running = false;
  let remaining = 60;
  let timerHandle = null;
  let current = null;
  let issuedAt = 0;

  function setTimerUI() { if (elTimer) elTimer.textContent = remaining; }
  function status(msg)  { if (elStatus) elStatus.textContent = msg; }

  async function getStimulus() {
    const res = await fetch(`/api/stimulus?sessionId=${encodeURIComponent(sessionId)}`);
    const data = await res.json();
    current = data;
    issuedAt = performance.now();
    if (elStimulus) {
      elStimulus.textContent = data.word;
      elStimulus.style.color = data.inkHex;
    }
    renderChoices(data.choices);
  }

  function renderChoices(arr) {
    if (!elChoices) return;
    elChoices.innerHTML = '';
    arr.forEach(c => {
      const b = document.createElement('button');
      b.textContent = c;
      if (cbMode && cbMode.checked) {
        b.style.borderWidth = '2px';
        b.style.textTransform = 'uppercase';
      }
      b.addEventListener('click', () => onChoose(c));
      elChoices.appendChild(b);
    });
  }

  async function onChoose(chosen) {
    if (!current) return;
    const latency = Math.max(0, Math.round(performance.now() - issuedAt));
    const payload = {
      stimulusId: current.id,
      chosenColor: chosen,
      clientLatencyMs: latency
    };
    const res = await fetch(`/api/submit?sessionId=${encodeURIComponent(sessionId)}`, {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(payload)
    });
    const data = await res.json();
    status(data.correct ? `✅ Correct (${latency} ms)` : `❌ Wrong (${latency} ms)`);

    if (running && remaining > 0) {
      setTimeout(getStimulus, 250);
    }
    refreshSummary();
  }

  async function refreshSummary() {
    try {
      const res = await fetch(`/api/summary?sessionId=${encodeURIComponent(sessionId)}`);
      const s = await res.json();
      if (elTrials)   elTrials.textContent = s.totalTrials;
      if (elCorrect)  elCorrect.textContent = s.correct;
      if (elAcc)      elAcc.textContent = s.accuracy.toFixed(1);
      if (elAvg)      elAvg.textContent = s.avgLatencyMs;
    } catch (_) {}
  }

  function tick() {
    remaining -= 1;
    setTimerUI();
    if (remaining <= 0) {
      clearInterval(timerHandle);
      running = false;
      status('⏱️ Time up!');
      current = null;
    }
  }

  if (elStart) {
    elStart.addEventListener('click', async () => {
      running = true;
      remaining = 60;
      setTimerUI();
      clearInterval(timerHandle);
      timerHandle = setInterval(tick, 1000);
      status('Get ready...');
      await getStimulus();
    });
  }

  // ===== Render the 4×4 matrix cells =====
  const PALETTE = {
    RED:    '#e53935',
    GREEN:  '#43a047',
    BLUE:   '#1e88e5',
    YELLOW: '#fdd835'
  };

  function initMatrixCells() {
    const cells = document.querySelectorAll('.matrix-cell');
    cells.forEach(cell => {
      const word = cell.getAttribute('data-word');
      const ink  = cell.getAttribute('data-ink');
      cell.textContent = word;
      cell.style.color = PALETTE[ink] || '#ffffff';
      if (cbMode && cbMode.checked) {
        // Optional accessibility tweaks on load
        cell.style.letterSpacing = '0.5px';
        cell.style.textTransform = 'uppercase';
      }
    });
  }

  // Initialize on load
  refreshSummary();
  initMatrixCells();
})();
