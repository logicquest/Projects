﻿'*********************************************************
'
' Copyright (c) Microsoft. All rights reserved.
' THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
' ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
' IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
' PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.
'
'*********************************************************

Imports System
Imports System.Collections.Generic
Imports Windows.ApplicationModel.DataTransfer
Imports Windows.Data.Html
Imports Windows.Storage.Streams
Imports Windows.UI.Xaml
Imports SDKTemplate
Imports System.Threading.Tasks
Imports Windows.Graphics.Imaging
Imports Windows.Storage
Imports Windows.Storage.Pickers
Imports Windows.UI.Core
Imports Windows.UI.Xaml.Controls
Imports Windows.UI.Xaml.Media.Imaging
Imports Windows.UI.Xaml.Navigation

Partial Public NotInheritable Class CopyText
    Inherits SDKTemplate.Common.LayoutAwarePage
    Private rootPage As MainPage = MainPage.Current

    Public Sub New()
        Me.InitializeComponent()
        Me.Init()
    End Sub

#Region "Scenario Specific Code"

    Private Sub Init()
        AddHandler CopyButton.Click, AddressOf CopyButton_Click
        AddHandler PasteButton.Click, AddressOf PasteButton_Click
        Description.NavigateToString(Me.htmlFragment)
    End Sub

#End Region

#Region "Button click"

    Private Sub CopyButton_Click(sender As Object, e As RoutedEventArgs)
        OutputText.Text = ""
        OutputResourceMapKeys.Text = ""
        OutputHtml.NavigateToString("<HTML></HTML>")

        ' Set the content to DataPackage as html format
        Dim htmlFormat As String = HtmlFormatHelper.CreateHtmlFormat(Me.htmlFragment)
        Dim dataPackage = New DataPackage()
        dataPackage.SetHtmlFormat(htmlFormat)

        ' Set the content to DataPackage as (plain) text format
        Dim plainText As String = HtmlUtilities.ConvertToText(Me.htmlFragment)
        dataPackage.SetText(plainText)

        ' Populate resourceMap with StreamReference objects corresponding to local image files embedded in HTML
        Dim imgUri = New Uri(imgSrc)
        Dim imgRef = RandomAccessStreamReference.CreateFromUri(imgUri)
        dataPackage.ResourceMap(imgSrc) = imgRef

        Try
            ' Set the DataPackage to clipboard.
            Windows.ApplicationModel.DataTransfer.Clipboard.SetContent(dataPackage)
            OutputText.Text = "Text and HTML formats have been copied to clipboard. "
        Catch ex As Exception
            ' Copying data to Clipboard can potentially fail - for example, if another application is holding Clipboard open
            rootPage.NotifyUser("Error copying content to Clipboard: " & ex.Message & ". Try again", NotifyType.ErrorMessage)
        End Try
    End Sub

    Private Async Sub PasteButton_Click(sender As Object, e As RoutedEventArgs)
        OutputText.Text = "Content in the clipboard: "
        OutputResourceMapKeys.Text = ""
        OutputHtml.NavigateToString("<HTML></HTML>")

        Dim dataPackageView = Windows.ApplicationModel.DataTransfer.Clipboard.GetContent()
        If dataPackageView.Contains(StandardDataFormats.Text) Then
            Try
                Dim text = Await dataPackageView.GetTextAsync()
                OutputText.Text = "Text: " & Environment.NewLine & text
            Catch ex As Exception
                rootPage.NotifyUser("Error retrieving Text format from Clipboard: " & ex.Message, NotifyType.ErrorMessage)
            End Try
        Else
            OutputText.Text = "Text: " & Environment.NewLine & "Text format is not available in clipboard"
        End If



        If dataPackageView.Contains(StandardDataFormats.Html) Then
            Me.DisplayResourceMapAsync(dataPackageView)

            Dim htmlFormat As String = Nothing
            Try
                htmlFormat = Await dataPackageView.GetHtmlFormatAsync()
            Catch ex As Exception
                rootPage.NotifyUser("Error retrieving HTML format from Clipboard: " & ex.Message, NotifyType.ErrorMessage)
            End Try

            If htmlFormat IsNot Nothing Then
                Dim htmlFragment As String = HtmlFormatHelper.GetStaticFragment(htmlFormat)
                OutputHtml.NavigateToString("HTML:<br/ > " & htmlFragment)
            End If
        Else
            OutputHtml.NavigateToString("HTML:<br/ > HTML format is not available in clipboard")
        End If
    End Sub

#End Region

#Region "private member variables"

    Private htmlFragment As String = "Use clipboard to copy and paste text in different formats, including plain text, and formatted text (HTML). <br />" & " To <b>copy</b>, add text formats to a <i>DataPackage</i>, and then place <i>DataPackage</i> to Clipboard.<br /> " & "To <b>paste</b>, get <i>DataPackageView</i> from clipboard, and retrieve the content of the desired text format from it.<br />" & "To handle local image files in the formatted text (such as the one below), use ResourceMap collection." & "<br /><img id=""scenario1LocalImage"" src=""" & imgSrc & """ /><br />" & "<i>DataPackage</i> class is also used to share or send content between applications. <br />" & "See the Share SDK sample for more information."

    Private Const imgSrc As String = "ms-appx-web:///assets/windows-sdk.png"

#End Region

#Region "helper functions"

    ' Note: this sample is not trying to resolve and render the HTML using resource map. 
    ' Please refer to the Clipboard JavaScript sample for an example of how to use resource map 
    ' for local images display within an HTML format. This sample will only demonstrate how to
    ' get a resource map object and extract its key values
    Private Async Sub DisplayResourceMapAsync(dataPackageView As DataPackageView)
        OutputResourceMapKeys.Text = Environment.NewLine & "Resource map: "
        Dim resMap As IReadOnlyDictionary(Of String, RandomAccessStreamReference) = Nothing
        Try
            resMap = Await dataPackageView.GetResourceMapAsync()
        Catch ex As Exception
            rootPage.NotifyUser("Error retrieving Resource map from Clipboard: " & ex.Message, NotifyType.ErrorMessage)
        End Try

        If resMap IsNot Nothing Then
            If resMap.Count > 0 Then
                For Each item In resMap
                    OutputResourceMapKeys.Text &= Environment.NewLine & "Key: " & item.Key
                Next
            Else
                OutputResourceMapKeys.Text &= Environment.NewLine & "Resource map is empty"
            End If
        End If
    End Sub

#End Region
End Class
