<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

	<title>VDI Portal - TUD FB20</title>

	<!-- jQuery -->
	<script type="text/javascript" src="../resources/js/jquery-1.6.2.min.js"> </script>
	<script type="text/javascript" src="../resources/js/jquery-ui-1.8.16.custom.min.js"> </script>
	<link rel="stylesheet" type="text/css" href="../resources/js/smoothness/jquery-ui-1.8.16.custom.css" />

	<!-- Direct Web Remoting -->
	<script type='text/javascript' src='../dwr/engine.js'> </script>
	<script type='text/javascript' src='../dwr/interface/Manager.js'> </script>
	<script type='text/javascript' src='../dwr/util.js'> </script>

	<!-- Fancybox -->
	<link rel="stylesheet" type="text/css" href="../resources/fancybox/jquery.fancybox-1.3.4.css" />
	<script type="text/javascript" src="../resources/fancybox/jquery.fancybox-1.3.4.pack.js"> </script>

	<link rel="stylesheet" type="text/css" href="../resources/css/interface.css" />
	<script type="text/javascript" src="../resources/js/interface.js"> </script>
</head>
<body>

	<div class="vdi-view">

		<div class="vdi-header">
			<h1>Meine VMs</h1>
		</div>

		<div class="vdi-control">
			<a class="vdi-create-vm" href="#vdi-create-vm-dialog">Neue VM</a>
			<a class='vdi-mount-image' href='#vdi-mount-image-dialog'></a>
		</div>

		<div class="vdi-nav">
			<h2>Tags</h2>
			<ul>
			</ul>
		</div>

		<div class="vdi-content">
			<div class="vdi-machine-drawer">
				<div class="vdi-machine-drawer-machines"></div>
				<div class="clear-layout"></div>
			</div>
		</div>

		<div class="clear-layout"></div>
	</div>

	<div class="vdi-dialog-container">
		<div id="vdi-create-vm-dialog" class="vdi-dialog">
			<h2>Neue VM erstellen</h2>

			<div class="vdi-dialog-option">
				<div class="vdi-dialog-option-label">
					<label for="vdi-create-vm-name">Name:</label>
				</div>
				<div class="vdi-dialog-option-input">
					<input type="text" id="vdi-create-vm-name" />
				</div>
			</div>
			<div class="vdi-dialog-option">
				<div class="vdi-dialog-option-label">
					<label for="vdi-create-vm-type">Typ:</label>
				</div>
				<div class="vdi-dialog-option-input">
					<span class="vdi-dialog-option-input-description">Betriebssystem:</span>
					<select id="vdi-create-vm-type-family">
						<option value=''></option>
					</select>
					<br />
					<span class="vdi-dialog-option-input-description">Version:</span>
					<select id="vdi-create-vm-type"></select>
				</div>
			</div>
			<div class="vdi-dialog-option">
				<div class="vdi-dialog-option-label">
					<label for="vdi-create-vm-description">Beschreibung:</label>
				</div>
				<div class="vdi-dialog-option-input">
					<input type="text" id="vdi-create-vm-description" />
				</div>
			</div>
			<div class="vdi-dialog-option">
				<div class="vdi-dialog-option-label">
					<label for="vdi-create-vm-memory">RAM (MB):</label>
				</div>
				<div class="vdi-dialog-option-input">
					<input type="text" id="vdi-create-vm-memory" />
				</div>
			</div>
			<div class="vdi-dialog-option">
				<div class="vdi-dialog-option-label">
					<label for="vdi-create-vm-harddrive">HDD (GB):</label>
				</div>
				<div class="vdi-dialog-option-input">
					<input type="text" id="vdi-create-vm-harddrive" />
				</div>
			</div>
			<div class="vdi-dialog-option">
				<div class="vdi-dialog-option-label">
					<label for="vdi-create-vm-vram">VRAM (MB):</label>
				</div>
				<div class="vdi-dialog-option-input">
					<input type="text" id="vdi-create-vm-vram" />
				</div>
			</div>
			<div class="vdi-dialog-option">
				<div class="vdi-dialog-option-label">
					<label for="vdi-create-vm-2d-acceleration">2D Acceleration:</label>
				</div>
				<div class="vdi-dialog-option-input">
					<input type="checkbox" id="vdi-create-vm-2d-acceleration" />
				</div>
			</div>
			<div class="vdi-dialog-option">
				<div class="vdi-dialog-option-label">
					<label for="vdi-create-vm-3d-acceleration">3D Acceleration:</label>
				</div>
				<div class="vdi-dialog-option-input">
					<input type="checkbox" id="vdi-create-vm-3d-acceleration" />
				</div>
			</div>
			<div class="vdi-dialog-option">
				<div class="vdi-dialog-option-label">
					<label for="vdi-create-vm-tags">Tags:</label>
				</div>
				<div class="vdi-dialog-option-input">
					<input type="text" id="vdi-create-vm-tags" />
				</div>
			</div>

			<div class="dialog-button">
				<button class="vdi-create-vm-button">Erstellen</button>
			</div>
		</div>

		<div id="vdi-mount-image-dialog" class="vdi-dialog">
			<h2>Image mounten</h2>

			<input type="hidden" id="vdi-mount-image-machine-id">

			<div class="vdi-mount-image-machine-info">
				<span class="vdi-mount-image-machine-name-title">VM: </span>
				<span class="vdi-mount-image-machine-name"></span>
			</div>

			<div class="vdi-dialog-option">
				<label for="vdi-mount-image-identifier">Image:</label>
				<select id="vdi-mount-image-identifier"></select>
			</div>

			<div class="dialog-button">
				<button class="vdi-mount-image-button">Einbinden</button>
			</div>
		</div>
	</div>

</body>
</html>
