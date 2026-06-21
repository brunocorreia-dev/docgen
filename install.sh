#!/usr/bin/env bash
set -euo pipefail

REPO="brunocorreia-dev/docgen"
JAR_URL="https://github.com/${REPO}/releases/latest/download/docgen.jar"
INSTALL_DIR="$HOME/.local/share/docgen"
BIN_DIR="$HOME/.local/bin"
WRAPPER="$BIN_DIR/docgen"

# ── colours ──────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
info()  { echo -e "${GREEN}[docgen]${NC} $*"; }
warn()  { echo -e "${YELLOW}[docgen]${NC} $*"; }
error() { echo -e "${RED}[docgen] Error:${NC} $*" >&2; exit 1; }

# ── prereq: Java 21+ ─────────────────────────────────────────────────────────
if ! command -v java &>/dev/null; then
  error "Java not found. Install Java 21+ and re-run.\n  https://adoptium.net"
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | sed 's/.*version "\([0-9]*\).*/\1/')
if (( JAVA_VERSION < 21 )); then
  error "Java 21+ required (found Java ${JAVA_VERSION}).\n  https://adoptium.net"
fi
info "Java ${JAVA_VERSION} found."

# ── prereq: Ollama (optional warning) ────────────────────────────────────────
if ! command -v ollama &>/dev/null; then
  warn "Ollama not found. docgen needs Ollama to run."
  warn "Install it from https://ollama.com, then: ollama pull llama3.2"
fi

# ── download jar ─────────────────────────────────────────────────────────────
info "Downloading docgen.jar from GitHub releases..."
mkdir -p "$INSTALL_DIR"

if command -v curl &>/dev/null; then
  curl -fsSL "$JAR_URL" -o "$INSTALL_DIR/docgen.jar"
elif command -v wget &>/dev/null; then
  wget -qO "$INSTALL_DIR/docgen.jar" "$JAR_URL"
else
  error "Neither curl nor wget found. Install one and re-run."
fi
info "Saved to $INSTALL_DIR/docgen.jar"

# ── install wrapper ───────────────────────────────────────────────────────────
mkdir -p "$BIN_DIR"
cat > "$WRAPPER" <<'WRAPPER_SCRIPT'
#!/usr/bin/env bash
set -euo pipefail
JAR="$HOME/.local/share/docgen/docgen.jar"
if [[ ! -f "$JAR" ]]; then
  echo "docgen: jar not found at $JAR — run the install script again." >&2
  exit 1
fi
exec java -jar "$JAR" "$@"
WRAPPER_SCRIPT
chmod +x "$WRAPPER"
info "Wrapper installed at $WRAPPER"

# ── PATH hint ────────────────────────────────────────────────────────────────
if ! echo "$PATH" | tr ':' '\n' | grep -qx "$BIN_DIR"; then
  warn "$BIN_DIR is not in your PATH."
  warn "Add this to your shell profile (~/.bashrc or ~/.zshrc):"
  echo ""
  echo '  export PATH="$HOME/.local/bin:$PATH"'
  echo ""
fi

# ── done ─────────────────────────────────────────────────────────────────────
echo ""
info "Installation complete!"
echo ""
echo "Usage:"
echo "  # Start Ollama first"
echo "  ollama serve &"
echo "  ollama pull llama3.2"
echo ""
echo "  # Generate docs for any repo"
echo "  docgen /path/to/repo -o /path/to/output"
echo "  docgen /path/to/repo -m mistral"
echo ""
echo "  # Get help"
echo "  docgen --help"
