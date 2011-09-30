<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	
	<meta name="viewport" content="width=1024px" />

	<title>VDI Portal - TUD FB20</title>

	<!-- jQuery -->
	<script type="text/javascript" src="../resources/js/jquery-1.6.2.min.js"> </script>
	<script type="text/javascript" src="../resources/js/jquery-ui-1.8.16.custom.min.js"> </script>
	<link rel="stylesheet" type="text/css" href="../resources/js/smoothness/jquery-ui-1.8.16.custom.css" />

	<!-- Direct Web Remoting -->
	<script type='text/javascript' src='../dwr/engine.js'> </script>
	<script type='text/javascript' src='../dwr/interface/Manager.js'> </script>
	<script type='text/javascript' src='../dwr/util.js'> </script>

	<!-- Bootstrap -->
	<script type="text/javascript" src="../resources/js/bootstrap-modal.js"> </script>
	<link rel="stylesheet" type="text/css" href="../resources/css/bootstrap.min.css" />

	<link rel="stylesheet" type="text/css" href="../resources/css/interface.css" />
	<script type="text/javascript" src="../resources/js/interface.js"> </script>
</head>
<body>

	<div class="container">

		<div class="content">

			<div class="page-header">
				<button class="btn large primary vdi-create-vm">
					<span>+</span> Neue VM
				</button>

				<h1>Meine VMs</h1>
			</div>

			<div class="row">
			
				<div class="span3">
					<h2>Tags</h2>

					<ul class="pills tag-nav"></ul>
				</div>

				<div class="span13">
					<div class="vdi-machine-drawer">
						<div class="vdi-machine-drawer-machines"></div>
						<div class="clear-layout"></div>
					</div>
				</div>

			</div>

		</div>

		<footer>
			<p>&copy; RBG TU Darmstadt 2011</p>
		</footer>
	</div>

	<div id="vdi-create-vm-dialog" class="modal hide fade">
		<div class="modal-header">
			<a href="#" class="close">&times;</a>
			<h3>Neue VM erstellen</h3>
		</div>
		<form>
			<div class="modal-body">
				<fieldset>
					<div class="clearfix">
						<label for="vdi-create-vm-name">Name</label>
						<div class="input">
							<input class="span4" id="vdi-create-vm-name" type="text">
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-create-vm-description">Beschreibung</label>
						<div class="input">
							<input class="span4" id="vdi-create-vm-description" type="text">
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-create-vm-type-family">Betriebssystem Typ</label>
						<div class="input">
							<select class="span4" id="vdi-create-vm-type-family">
								<option value=''></option>
							</select>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-create-vm-type">Betriebssystem Vers.</label>
						<div class="input">
							<select class="span4" id="vdi-create-vm-type">
								<option value=''></option>
							</select>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-create-vm-image">ISO Image einbinden</label>
						<div class="input">
							<select class="span4" id="vdi-create-vm-image">
								<option value=''></option>
							</select>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-create-vm-memory">Arbeitsspeicher (MB)</label>
						<div class="input">
							<div id="vdi-create-vm-memory-slider" class="span4 vdi-slider"></div>
							<span class="span2 uneditable-input vdi-slider-output" id="vdi-create-vm-memory"></span>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-create-vm-harddrive">Festplatte (GB)</label>
						<div class="input">
							<div id="vdi-create-vm-harddrive-slider" class="span4 vdi-slider"></div>
							<span class="span2 uneditable-input vdi-slider-output" id="vdi-create-vm-harddrive"></span>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-create-vm-vram">Video RAM (MB)</label>
						<div class="input">
							<div id="vdi-create-vm-vram-slider" class="span4 vdi-slider"></div>
							<span class="span2 uneditable-input vdi-slider-output" id="vdi-create-vm-vram"></span>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-create-vm-2d-acceleration">2D Beschleunigung</label>
						<div class="input">
							<ul class="inputs-list">
								<li>
									<input id="vdi-create-vm-2d-acceleration" type="checkbox">
								</li>
							</ul>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-create-vm-3d-acceleration">3D Beschleunigung</label>
						<div class="input">
							<ul class="inputs-list">
								<li>
									<input id="vdi-create-vm-3d-acceleration" type="checkbox">
								</li>
							</ul>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-create-vm-tags">Tags</label>
						<div class="input">
							<input class="span4" id="vdi-create-vm-tags" type="text">
						</div>
					</div>
				</fieldset>
			</div>
			<div class="modal-footer">
				<button type="submit" class="btn primary">Erstellen</button>
				<span class="btn secondary">Abbrechen</span>
			</div>
		</form>
	</div>

	<div id="vdi-edit-vm-dialog" class="modal hide fade">
		<div class="modal-header">
			<a href="#" class="close">&times;</a>
			<h3>VM bearbeiten</h3>
		</div>
		<form>
			<div class="modal-body">
				<fieldset>
					<input type="hidden" id="vdi-edit-vm-machine-id">
					<div class="clearfix">
						<label for="vdi-edit-vm-name">Name</label>
						<div class="input">
							<input class="span4" id="vdi-edit-vm-name" type="text">
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-edit-vm-description">Beschreibung</label>
						<div class="input">
							<input class="span4" id="vdi-edit-vm-description" type="text">
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-edit-vm-memory">Arbeitsspeicher (MB)</label>
						<div class="input">
							<div id="vdi-edit-vm-memory-slider" class="span4 vdi-slider"></div>
							<span class="span2 uneditable-input vdi-slider-output" id="vdi-edit-vm-memory"></span>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-edit-vm-vram">Video RAM (MB)</label>
						<div class="input">
							<div id="vdi-edit-vm-vram-slider" class="span4 vdi-slider"></div>
							<span class="span2 uneditable-input vdi-slider-output" id="vdi-edit-vm-vram"></span>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-edit-vm-2d-acceleration">2D Beschleunigung</label>
						<div class="input">
							<ul class="inputs-list">
								<li>
									<input id="vdi-edit-vm-2d-acceleration" type="checkbox">
								</li>
							</ul>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-edit-vm-3d-acceleration">3D Beschleunigung</label>
						<div class="input">
							<ul class="inputs-list">
								<li>
									<input id="vdi-edit-vm-3d-acceleration" type="checkbox">
								</li>
							</ul>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-edit-vm-tags">Tags</label>
						<div class="input">
							<input class="span4" id="vdi-edit-vm-tags" type="text">
						</div>
					</div>
				</fieldset>
				<p>
					<span class="label notice">Notice</span>
					Änderungen an RAM, VRAM, 2D &amp; 3D Beschleunigung werden erst beim nächsten Start der Maschine aktiv.
				</p>
			</div>
			<div class="modal-footer">
				<button type="submit" class="btn primary">Bearbeiten</button>
				<span class="btn secondary">Abbrechen</span>
			</div>
		</form>
	</div>

	<div id="vdi-mount-image-dialog" class="modal hide fade">
		<div class="modal-header">
			<a href="#" class="close">&times;</a>
			<h3>ISO Image einbinden</h3>
		</div>
		<form>
			<div class="modal-body">
				<fieldset>
					<input type="hidden" id="vdi-mount-image-machine-id">
					<div class="clearfix">
						<label for="vdi-mount-image-name">VM Name</label>
						<div class="input">
							<span class="uneditable-input" id="vdi-mount-image-name"></span>
						</div>
					</div>
					<div class="clearfix">
						<label for="vdi-mount-image-identifier">ISO Image</label>
						<div class="input">
							<select class="span4" id="vdi-mount-image-identifier"></select>
						</div>
					</div>
				</fieldset>
			</div>
			<div class="modal-footer">
				<button type="submit" class="btn primary">Einbinden</button>
				<span class="btn secondary">Abbrechen</span>
			</div>
		</form>
	</div>

	<div id="vdi-delete-vm-dialog" class="modal hide fade">
		<div class="modal-header">
			<a href="#" class="close">&times;</a>
			<h3>VM löschen</h3>
		</div>
			<input type="hidden" id="vdi-delete-vm-machine-id">
			<div class="modal-body">
				<p>Soll die VM '<span id="vdi-delete-vm-name"></span>' wirklich gelöscht werden?</p>
			</div>
		<div class="modal-footer">
			<button class="btn danger">Löschen</button>
			<span class="btn secondary">Abbrechen</span>
		</div>
	</div>

</body>
</html>
