#!/data/data/com.termux/files/usr/bin/bash
# install-to-app.sh — Copy bareiron binary where the Android app can find it
# Run this on your Termux device after building the server

set -e

BINARY_SRC=""
DEST_DIR="/data/data/com.bareiron.game/files"

# Auto-detect binary location
for path in \
    "$(pwd)/bareiron" \
    "$(pwd)/server/bareiron" \
    "$HOME/bareiron" \
    "$HOME/Termux-Mobile-BAREIRON-server/bareiron" \
    "$HOME/Termux-BAREIRON-server-to--andriod-app/server/bareiron"
do
    if [ -f "$path" ] && [ -x "$path" ]; then
        BINARY_SRC="$path"
        echo "✅ Found binary: $BINARY_SRC"
        break
    fi
done

if [ -z "$BINARY_SRC" ]; then
    echo "❌ Could not find bareiron binary."
    echo "Build it first:"
    echo "  cd Termux-Mobile-BAREIRON-server"
    echo "  ./build.sh"
    exit 1
fi

# Check if app is installed
if [ ! -d "$DEST_DIR" ]; then
    echo "⚠️  App data directory does not exist yet."
    echo "   Install the BAREIRON app first, then run this script."
    echo "   Or create directory manually:"
    echo "     mkdir -p $DEST_DIR"
    exit 1
fi

echo "📦 Copying binary to app storage..."
cp "$BINARY_SRC" "$DEST_DIR/bareiron"
chmod +x "$DEST_DIR/bareiron"

echo "✅ Done! Binary installed at:"
echo "   $DEST_DIR/bareiron"
echo ""
echo "Now open the BAREIRON app and tap SINGLE PLAYER."
